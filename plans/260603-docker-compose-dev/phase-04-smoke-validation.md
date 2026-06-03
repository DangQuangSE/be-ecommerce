# Phase 4: Smoke Validation

## Requirements

Xác nhận toàn bộ Docker Compose stack hoạt động đúng từ đầu đến cuối trên môi trường local, và đảm bảo CI/CD pipeline hiện tại không bị ảnh hưởng.

## Steps

1. Kiểm tra **DB-only flow**: Chạy `docker compose up -d`, đợi `healthy`, sau đó kết nối vào DB bằng psql hoặc DBeaver để xác nhận schema được tạo đúng (Hibernate `ddl-auto=update` sẽ tạo tables khi API start lần đầu — ở bước này chỉ cần DB kết nối được).

2. Kiểm tra **full-stack flow**: Build JAR với `mvn clean package -DskipTests`, copy/rename JAR thành `app.jar`, chạy `docker compose --profile full up -d`, theo dõi `docker compose logs -f api` cho đến khi thấy "Started SportProBeApplication".

3. Kiểm tra **API health**: Gửi HTTP request tới endpoint public (ví dụ `GET /api/products` hoặc route auth public) — xác nhận HTTP response (2xx/401), không connection refused.

4. Kiểm tra **DB connectivity từ API**: Xem logs không có lỗi `Connection refused` hay `FATAL: password authentication failed`. Gọi một endpoint đọc dữ liệu từ DB (ví dụ danh sách sản phẩm) để xác nhận query chạy thành công.

5. Kiểm tra **isolation profile**: Chạy `docker compose down` rồi `docker compose up -d` (không có `--profile full`) — xác nhận chỉ `postgres` service start, `api` không start.

6. Kiểm tra **CI không bị ảnh hưởng**: Xem `deploy-vm.yml` không tham chiếu `docker-compose.yml`. Nếu repo có CI pipeline chạy tests, xác nhận pipeline vẫn pass (thực tế là chạy `git diff .github/` để xem không có thay đổi nào).

## Success Criteria

- `docker compose ps` sau bước 1: `postgres` ở trạng thái `healthy`
- `docker compose ps` sau bước 2: cả `postgres` và `api` ở trạng thái `running` hoặc `healthy`
- Bước 3: HTTP 200 từ `localhost:8080`
- Bước 4: Không có lỗi DB trong logs, ít nhất một endpoint trả dữ liệu
- Bước 5: Chỉ một service trong `docker compose ps`
- Bước 6: `git diff .github/` không có output

## Risks

- `Started SportProBeApplication` không xuất hiện trong logs sau 60 giây: Kiểm tra healthcheck timeout, tăng `start_period` trong healthcheck nếu cần
- API thấy lỗi authentication với DB (`password authentication failed`): Xóa volume cũ bằng `docker compose down -v` và thử lại — postgres lưu credentials lần đầu tạo volume
- Windows path issue với JAR copy: Hướng dẫn dùng Git Bash hoặc PowerShell copy command thay vì Linux `cp`
