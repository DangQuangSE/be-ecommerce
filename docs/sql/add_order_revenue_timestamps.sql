BEGIN;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS paid_at TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivered_at TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS spending_credited BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS spending_reversed BOOLEAN NOT NULL DEFAULT FALSE;
CREATE INDEX IF NOT EXISTS idx_orders_realized_revenue ON orders (delivered_at) WHERE status = 'DELIVERED' AND payment_completed = TRUE;
COMMIT;
-- Rollout: backup, schema migration, then compatible code deploy.
-- Legacy backfill is a separate operator-approved migration.
-- Roll back only before deploying code: DROP INDEX idx_orders_realized_revenue;
-- ALTER TABLE orders DROP COLUMN delivered_at, DROP COLUMN paid_at,
--   DROP COLUMN spending_credited, DROP COLUMN spending_reversed;
