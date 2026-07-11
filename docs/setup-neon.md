# Hướng dẫn Setup Neon PostgreSQL

## 1. Tạo Database trên Neon

1. Vào https://console.neon.tech
2. Đăng nhập (GitHub/Google/Email)
3. Click **New Project**
4. Đặt tên: `sport-pro-db`
5. Region: chọn gần nhất (VD: Singapore)
6. Click **Create Project**

## 2. Lấy thông tin kết nối

Trong dashboard Neon → **Connection Details**:

```
Host: ep-xxxx.aws.neon.tech
Port: 5432
Database: neondb
User: neondb_owner
Password: npg_xxxxx
```

## 3. Cập nhật `.env` trên VPS

```env
DB_HOST=ep-xxxx.aws.neon.tech
DB_PORT=5432
DB_NAME=neondb
DB_USERNAME=neondb_owner
DB_PASSWORD=npg_xxxxx
```

## 4. Backend tự động migrate

Spring Boot với `ddl-auto=update` sẽ tự động tạo bảng khi start.

## 5. Quản lý dữ liệu qua SQL Editor

1. Vào Neon Console → chọn project
2. Click **SQL Editor** (sidebar trái)
3. Chạy các lệnh SQL trực tiếp

### Tạo Admin User

**Cách 1 — Seeder tự động (khuyến nghị cho local/Docker):**

Thêm vào `.env`:

```env
APP_ADMIN_SEED_ENABLED=true
APP_ADMIN_EMAIL=admin@sportpro.local
APP_ADMIN_PASSWORD=Admin@123456
```

Restart API. Seeder tạo user `ADMIN` nếu email chưa tồn tại. Chỉ bật trên môi trường dev — **không** set `APP_ADMIN_SEED_ENABLED=true` trên production.

Đăng nhập: `POST /api/auth/login` với email/password trên.

**Cách 2 — SQL thủ công (fallback):**

Sau khi register tài khoản qua API, chạy:

```sql
UPDATE app_users SET role = 'ADMIN' WHERE email = 'your-email@gmail.com';
```

Nếu email đã tồn tại với role `USER`, seeder **không** tự nâng lên `ADMIN` — dùng SQL hoặc đổi email trong `.env`.

### Tạo Sample Customer User

**Seeder tự động (chỉ dev/local), cùng cơ chế với Admin Seed ở trên:**

Thêm vào `.env`:

```env
APP_CUSTOMER_SEED_ENABLED=true
APP_CUSTOMER_EMAIL=customer@sportpro.local
APP_CUSTOMER_PASSWORD=Customer@123456
```

Restart API. Seeder tạo user `USER` nếu email chưa tồn tại. Chỉ bật trên môi trường dev — **không** set `APP_CUSTOMER_SEED_ENABLED=true` trên production.

Đăng nhập: `POST /api/auth/login` với email/password trên.

### Xem danh sách users:

```sql
SELECT id, email, role, email_verified FROM app_users;
```

## 6. Lưu ý

- Neon free tier: 0.5GB storage, không giới hạn requests
- Auto-pause sau 5 phút không hoạt động (khởi động lại ~1-2s khi có request đầu)
- Không cần migrate lên Azure — Neon serverless đủ dùng cho giai đoạn đầu
