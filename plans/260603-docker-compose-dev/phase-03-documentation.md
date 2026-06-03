# Phase 3: Tài liệu hướng dẫn

## Requirements

Viết `docs/docker-compose.md` với hướng dẫn đầy đủ để thành viên mới trong nhóm có thể setup và chạy project từ đầu bằng Docker trong vòng 5 phút, không cần biết trước về cấu hình dự án.

## Steps

1. Tạo thư mục `docs/` nếu chưa có. Tạo file `docs/docker-compose.md` với cấu trúc rõ ràng: Prerequisites, Setup, Các lệnh thường dùng, Troubleshooting.

2. Viết phần **Prerequisites**: Docker Desktop >= 4.x, JDK 21 (nếu muốn chạy full stack), lệnh kiểm tra phiên bản (`docker compose version`).

3. Viết phần **Setup lần đầu**: hướng dẫn `cp .env.example .env`, điền `DB_*`, JWT, refresh-token cookie vars, Cloudinary (nếu dùng upload). Giải thích `DB_HOST=localhost` giữ nguyên cho IDE; compose override `DB_HOST=postgres` khi chạy profile `full`.

   Bổ sung `.env.example` các biến thiếu (bắt buộc cho startup): `APP_REFRESH_TOKEN_EXP_DAYS`, `APP_REFRESH_TOKEN_COOKIE_NAME`, `APP_REFRESH_TOKEN_COOKIE_SECURE`, `APP_REFRESH_TOKEN_COOKIE_SAMESITE`, `APP_REFRESH_TOKEN_COOKIE_PATH`, và `APP_FORGOT_PASSWORD_*` nếu chưa có.

4. Viết phần **Các lệnh thường dùng** với bảng hoặc danh sách rõ ràng:
   - Chỉ chạy DB: `docker compose up -d`
   - Chạy full stack (DB + API): `mvn clean package -DskipTests`, copy JAR → `app.jar` (Linux: `cp target/*.jar app.jar`; Windows: `Copy-Item (Get-Item target\*.jar) app.jar`), rồi `docker compose --profile full up -d --build`
   - Xem logs: `docker compose logs -f api`
   - Dừng: `docker compose down`
   - Xóa data DB: `docker compose down -v`

5. Viết phần **Troubleshooting** với các lỗi phổ biến:
   - `port 5432 already in use` → hướng dẫn đổi host port
   - `app.jar not found` khi build image → nhắc chạy `mvn package` trước
   - API lỗi `Connection refused` → kiểm tra `docker compose logs postgres` và healthcheck status
   - `DB_HOST=localhost` không hoạt động trong container → giải thích networking Docker

6. Cập nhật `README.md` gốc (nếu tồn tại) bằng cách thêm một mục ngắn "Local Development with Docker" với link đến `docs/docker-compose.md`.

## Success Criteria

- File `docs/docker-compose.md` tồn tại và có đủ 4 phần chính
- Một thành viên mới đọc tài liệu và chạy thành công mà không cần hỏi thêm
- Không có bước nào giả định kiến thức về cấu hình nội bộ của project

## Risks

- README.md không tồn tại: Bỏ qua bước cập nhật README, chỉ tạo `docs/docker-compose.md`
- Tài liệu bị lỗi thời nếu env vars thay đổi: `.env.example` là nguồn thực tế duy nhất — docs chỉ tham chiếu đến nó, không duplicate danh sách vars
