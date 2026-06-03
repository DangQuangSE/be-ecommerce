# Phase 1: PostgreSQL Service

## Requirements

Tạo `docker-compose.yml` với service `postgres` chạy ổn định, có persistent volume, healthcheck, và tất cả biến môi trường DB lấy từ `.env` — sẵn sàng để API kết nối vào.

## Steps

1. Tạo `docker-compose.yml` tại root của project với phần khai báo `services:` và `volumes:`. Chọn image `postgres:16-alpine` cho service `postgres`.

2. Khai báo các biến môi trường cho postgres (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`) bằng cách ánh xạ trực tiếp từ các biến `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` có trong `.env`.

3. Thêm named volume `postgres_data` để dữ liệu không mất khi container restart. Mount volume vào `/var/lib/postgresql/data`.

4. Thêm `healthcheck` cho postgres service: dùng `pg_isready` để compose biết khi nào DB thực sự sẵn sàng nhận kết nối (không chỉ dừng ở container started).

5. Expose port `5432` ra host machine để dev có thể kết nối bằng DBeaver hoặc psql trực tiếp. Ghi chú trong file về cách đổi host port nếu bị conflict.

6. Cập nhật `.env.example` nếu cần — thêm comment ghi rõ `DB_HOST=postgres` khi chạy trong Docker, `DB_HOST=localhost` khi chạy IDE trực tiếp.

## Success Criteria

- `docker compose up -d` không có lỗi, `docker compose ps` hiển thị `postgres` ở trạng thái `healthy`
- Kết nối được vào DB bằng psql hoặc DBeaver với thông tin từ `.env`
- Volume `postgres_data` tồn tại: `docker volume ls | grep postgres_data`
- Không có file nào trong `.github/workflows/` bị sửa đổi

## Risks

- Port 5432 đã được PostgreSQL local chiếm: Sửa host port thành `5433:5432` trong `docker-compose.yml`
- Postgres container start nhưng chưa ready khi API kết nối: Healthcheck + `depends_on: condition: service_healthy` giải quyết ở Phase 2
