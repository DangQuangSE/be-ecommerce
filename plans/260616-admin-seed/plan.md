# Plan: Seed Admin Account

Status: 🟢 Complete  

## Session Notes
<!-- Updated by cook automatically — do not edit manually -->

**Last active:** 2026-06-16
**Phase in progress:** (done)
**Status:** Admin seeder implemented; credentials admin@sportpro.local / Admin@123456

### Decisions made this session
- Skip existing email (no role upgrade) per user confirmation
- Gate only via APP_ADMIN_SEED_ENABLED=true
- Docker Compose docs updated alongside setup-neon.md

### Next immediate action
- User sets APP_ADMIN_SEED_ENABLED=true in .env and runs docker compose up
Date: 2026-06-16  
Mode: Fast  
Testing: Manual smoke test (không yêu cầu automated test suite mới)

## Overview

Thêm `AdminSeederRunner` — `CommandLineRunner` idempotent — tự động tạo **một** tài khoản `ADMIN` khi app khởi động, nếu email chưa tồn tại. Credentials lấy từ biến môi trường, không hardcode. Thay thế quy trình thủ công `UPDATE app_users SET role = 'ADMIN'` trong `docs/setup-neon.md`.

## Scope Challenge

| | |
|---|---|
| **Exists?** | Chỉ có hướng dẫn SQL thủ công trong `docs/setup-neon.md` — chưa có seeder tự động |
| **Minimum?** | 1 class `AdminSeederRunner` + 3 env vars + cập nhật `.env.example` |
| **Complexity** | Fast — 1 component mới, pattern đã có (`PrintingSeederRunner`) |

## Phases

- [x] Phase 1: Admin Seeder Runner — Tạo `AdminSeederRunner` với logic idempotent, BCrypt hash, role ADMIN
- [x] Phase 2: Env & Docs — Thêm biến môi trường vào `.env.example`, dotenv loader, cập nhật `docs/setup-neon.md`

## Approach

Theo đúng pattern `PrintingSeederRunner`:

```
App startup
  → AdminSeederRunner.run()
  → if APP_ADMIN_SEED_ENABLED != true → skip (log info)
  → if email/password blank → skip (log warn)
  → if existsByEmailIgnoreCase(email) → skip (log info)
  → else create User(role=ADMIN, emailVerified=true, isActive=true)
```

**Env vars đề xuất:**

| Biến | Mô tả | Ví dụ |
|------|-------|-------|
| `APP_ADMIN_SEED_ENABLED` | Bật/tắt seeder | `true` (chỉ dev/local) |
| `APP_ADMIN_EMAIL` | Email admin | `admin@sportpro.local` |
| `APP_ADMIN_PASSWORD` | Mật khẩu plain (hash BCrypt khi seed) | `Admin@123` |

Seeder **không chạy** khi `APP_ADMIN_SEED_ENABLED` không phải `true` — tránh seed nhầm trên production.

## Dependencies

- `UserRepository`, `PasswordEncoder` (BCrypt — bean sẵn có trong `SecurityConfig`)
- `Role.ADMIN` enum đã tồn tại
- `.env` file với 3 biến trên (dev/local)

## Risks

- **HIGH**: Seed admin trên production nếu quên tắt flag — Mitigation: gate bằng `APP_ADMIN_SEED_ENABLED=true` explicit; không default `true`
- **MEDIUM**: Password yếu trong `.env` — Mitigation: ghi rõ trong docs chỉ dùng local; không commit `.env`
- **LOW**: User đã tồn tại với role USER — Mitigation: seeder skip nếu email đã có (không upgrade role tự động; ghi rõ trong docs)

## Success Criteria (tổng thể)

1. Với `APP_ADMIN_SEED_ENABLED=true` + email/password hợp lệ → app start tạo 1 user `ADMIN` trong `app_users`
2. Restart app lần 2 → không tạo duplicate (idempotent)
3. `POST /api/auth/login` với credentials seeded → trả JWT
4. Endpoint yêu cầu `ROLE_ADMIN` (ví dụ admin user APIs) → 200, không 403
5. Với `APP_ADMIN_SEED_ENABLED=false` hoặc thiếu → không seed, app vẫn start bình thường
