# Revenue analytics rollout

1. Take and verify a logical database backup.
2. Apply `docs/sql/add_order_revenue_timestamps.sql` before deploying application code.
3. Deploy backend and confirm the legacy dashboard-summary contract remains available.
4. Run only the report section of `migrate_legacy_order_payment_delivery_timestamps.sql`.
5. Review candidate IDs and immutable payment evidence; do not infer online success from `DELIVERED` or `vnp_txn_ref`.
6. Populate the temporary approval table from audited evidence, obtain operator approval, then run the transaction.
7. Repeat reports and mutation postchecks; the second mutation must affect zero rows.
8. If reconciliation fails, stop deployment and restore the verified backup.

## Post-Cook verification

- Run lifecycle, analytics repository/service/controller/security, Flutter model/Cubit/widget and regression tests.
- Smoke `revenue-summary` with zero/nonzero previous periods and exact timezone boundaries.
- Rehearse migration twice on a disposable restored copy.
- Run `EXPLAIN (ANALYZE, BUFFERS)` for a five-year range against production-like cardinality and record p95.
- Exercise COD, verified VNPay, duplicate callback, unpaid prepaid delivery, refund removal and dashboard refresh.

## Evidence commands and sign-off

- Backup: `pg_dump --format=custom --file=revenue-pre-migration.dump "$DATABASE_URL"`; verify with `pg_restore --list revenue-pre-migration.dump` and attach output.
- Disposable rehearsal: restore the dump into a disposable database, run the SQL twice with `psql --set ON_ERROR_STOP=1 --file docs/sql/migrate_legacy_order_payment_delivery_timestamps.sql`, and attach both transcripts; the final idempotency query must return zero rows.
- Authenticated smoke: call both `/api/v1/admin/analytics/revenue-summary?startDate=2026-01-01&endDate=2026-01-07` and `/api/v1/admin/analytics/dashboard-summary` with an ADMIN bearer token; record HTTP 200 and preserve the legacy field names/types.
- E2E checklist: record IDs/results for COD, verified VNPay, duplicate callback, unpaid prepaid rejection, refund exclusion, boundary instants, picker presets and dashboard re-entry refresh.
- Query evidence: run `EXPLAIN (ANALYZE, BUFFERS)` for the repository SQL on production-like data; attach plan, cardinality, duration and measured p95 (<500 ms target).
- Restore drill: `pg_restore --clean --if-exists --dbname "$RESTORE_DATABASE_URL" revenue-pre-migration.dump`; verify row counts and sums before any shared-environment approval.
- Sign-off: operator ___ reviewer ___ environment ___ run_id ___ backup checksum ___ timestamp ___ verdict ___

No migration, Docker smoke, E2E scenario, benchmark, or production mutation is executed by Cook.
