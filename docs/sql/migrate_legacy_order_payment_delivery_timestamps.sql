-- REPORT-ONLY: run and review before opening the transaction below.
SELECT status, payment_method, payment_completed,
       COUNT(*) AS rows, COUNT(paid_at) AS with_paid_at, COUNT(delivered_at) AS with_delivered_at
FROM orders GROUP BY status, payment_method, payment_completed ORDER BY status, payment_method;

-- Auditable candidates. This schema has no persisted verified VNPay response/provider timestamp,
-- therefore vnp_txn_ref alone is NOT sufficient evidence and online rows remain unchanged.
SELECT id, vnp_txn_ref, payment_completed, paid_at, delivered_at, created_at
FROM orders WHERE payment_method = 'BANK_TRANSFER'
  AND (paid_at IS NULL OR (status = 'DELIVERED' AND delivered_at IS NULL));

-- COD candidates require explicit stakeholder approval. created_at is not a trustworthy delivery instant,
-- so this migration deliberately reports but does not infer timestamps.
SELECT id, payment_completed, status, paid_at, delivered_at, created_at
FROM orders WHERE payment_method = 'COD' AND status = 'DELIVERED'
  AND (paid_at IS NULL OR delivered_at IS NULL);

-- Durable evidence/audit staging. Populate only from immutable provider/audit records,
-- review it, then set approved_by and expected_rows. Never populate from order status alone.
CREATE TABLE IF NOT EXISTS revenue_timestamp_migration_evidence (
  run_id UUID NOT NULL, order_id BIGINT NOT NULL REFERENCES orders(id),
  evidence_class TEXT NOT NULL CHECK (evidence_class IN ('VNPAY_VERIFIED_IPN','COD_OPERATOR_APPROVED')),
  verified_paid_at TIMESTAMP NOT NULL, verified_delivered_at TIMESTAMP,
  evidence_reference TEXT NOT NULL, approved_by TEXT, expected_rows INTEGER,
  original_paid_at TIMESTAMP, original_delivered_at TIMESTAMP, migrated_at TIMESTAMP,
  PRIMARY KEY (run_id, order_id), UNIQUE (evidence_class, evidence_reference)
);

-- OPERATOR INPUT EXAMPLE (replace values; keep commented in source):
-- INSERT INTO revenue_timestamp_migration_evidence
-- (run_id,order_id,evidence_class,verified_paid_at,verified_delivered_at,evidence_reference,approved_by,expected_rows)
-- VALUES ('00000000-0000-0000-0000-000000000000',123,'VNPAY_VERIFIED_IPN','2025-01-01 01:00',NULL,'provider-audit-id','operator',1);

BEGIN;
DO $$ BEGIN
  IF EXISTS (SELECT 1 FROM revenue_timestamp_migration_evidence WHERE approved_by IS NULL OR expected_rows IS NULL) THEN
    RAISE EXCEPTION 'Every staged row requires approval and expected_rows';
  END IF;
  IF EXISTS (SELECT 1 FROM revenue_timestamp_migration_evidence GROUP BY run_id HAVING MIN(expected_rows) <> MAX(expected_rows) OR MIN(expected_rows) <> COUNT(*)) THEN
    RAISE EXCEPTION 'Expected evidence count mismatch';
  END IF;
END $$;

UPDATE revenue_timestamp_migration_evidence e SET original_paid_at=o.paid_at, original_delivered_at=o.delivered_at
FROM orders o WHERE o.id=e.order_id AND e.migrated_at IS NULL;

UPDATE orders o SET
  paid_at = COALESCE(o.paid_at, e.verified_paid_at),
  delivered_at = COALESCE(o.delivered_at, e.verified_delivered_at)
FROM revenue_timestamp_migration_evidence e
WHERE o.id = e.order_id AND o.payment_completed = TRUE
  AND e.approved_by IS NOT NULL AND e.migrated_at IS NULL
  AND ((e.evidence_class='VNPAY_VERIFIED_IPN' AND o.payment_method='BANK_TRANSFER')
    OR (e.evidence_class='COD_OPERATOR_APPROVED' AND o.payment_method='COD'))
  AND (o.paid_at IS NULL OR (o.delivered_at IS NULL AND e.verified_delivered_at IS NOT NULL));

UPDATE revenue_timestamp_migration_evidence SET migrated_at=CURRENT_TIMESTAMP
WHERE approved_by IS NOT NULL AND migrated_at IS NULL;

DO $$ BEGIN
  IF EXISTS (SELECT 1 FROM revenue_timestamp_migration_evidence e JOIN orders o ON o.id=e.order_id
    WHERE e.migrated_at IS NOT NULL AND (o.paid_at IS DISTINCT FROM COALESCE(e.original_paid_at,e.verified_paid_at)
      OR o.delivered_at IS DISTINCT FROM COALESCE(e.original_delivered_at,e.verified_delivered_at))) THEN
    RAISE EXCEPTION 'Post-update reconciliation failed';
  END IF;
END $$;

SELECT COUNT(*) AS remaining_ambiguous
FROM orders WHERE payment_completed = TRUE AND (paid_at IS NULL OR (status = 'DELIVERED' AND delivered_at IS NULL));
COMMIT;

SELECT run_id, COUNT(*) AS audited_rows, MIN(migrated_at) AS migrated_at,
       SUM(o.total_amount) AS audited_total, MIN(o.id) AS sample_min_id, MAX(o.id) AS sample_max_id
FROM revenue_timestamp_migration_evidence e JOIN orders o ON o.id=e.order_id
GROUP BY run_id;

-- Idempotency assertion: must return zero rows after a completed run.
SELECT * FROM revenue_timestamp_migration_evidence WHERE approved_by IS NOT NULL AND migrated_at IS NULL;

-- Re-run report-only queries: a second approved run must update zero rows.
-- Rollback after COMMIT requires restoring the mandatory pre-run logical backup.
