# Phase 2: Env & Docs

## Requirements

Wire biến môi trường admin seed vào dotenv loader và tài liệu, để dev biết cách bật seeder.

## Steps

1. Cập nhật `.env.example` — thêm section mới:

```env
# ==============================
# Admin Seed (local/dev only)
# ==============================
APP_ADMIN_SEED_ENABLED=false
APP_ADMIN_EMAIL=admin@sportpro.local
APP_ADMIN_PASSWORD=change-me-strong-password
```

2. Cập nhật `SportProBeApplication.java` — thêm `setIfPresent` cho:
   - `APP_ADMIN_SEED_ENABLED`
   - `APP_ADMIN_EMAIL`
   - `APP_ADMIN_PASSWORD`

3. Cập nhật `docs/setup-neon.md`:
   - Thay/bổ sung mục "Tạo Admin User" — ưu tiên dùng seeder tự động
   - Giữ SQL manual như fallback (upgrade role user đã register)
   - Ghi chú: chỉ bật `APP_ADMIN_SEED_ENABLED=true` trên local/dev, **không** trên production

## Files Changed

| File | Action |
|------|--------|
| `.env.example` | Update |
| `src/main/java/com/sport_pro_be/SportProBeApplication.java` | Update |
| `docs/setup-neon.md` | Update |

## Success Criteria

- Copy `.env.example` → `.env`, set `APP_ADMIN_SEED_ENABLED=true` + email/password → admin được seed
- `docs/setup-neon.md` mô tả rõ cả 2 cách: seeder tự động và SQL thủ công

## Manual Smoke Test

1. Set trong `.env`:
   ```
   APP_ADMIN_SEED_ENABLED=true
   APP_ADMIN_EMAIL=admin@test.local
   APP_ADMIN_PASSWORD=Admin@123456
   ```
2. Start app (`./mvnw spring-boot:run` hoặc Docker compose)
3. `POST /api/auth/login` body `{"email":"admin@test.local","password":"Admin@123456"}` → 200 + access token
4. Gọi endpoint admin (ví dụ `GET /api/admin/users` với Bearer token) → 200
5. Tắt seed (`APP_ADMIN_SEED_ENABLED=false`), restart → không tạo user mới

## Risks

- Dev quên tắt flag trước deploy — document rõ trong setup-neon.md
