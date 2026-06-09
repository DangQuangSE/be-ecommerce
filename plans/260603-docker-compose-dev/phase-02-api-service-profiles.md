# Phase 2: API Service với Compose Profiles

## Requirements

Thêm service `api` vào `docker-compose.yml` dưới Compose profile `full`, dùng lại `Dockerfile.runtime` hiện có. Dev chạy `docker compose --profile full up` để khởi động toàn bộ stack; chạy không có profile thì chỉ có DB.

## Steps

1. Thêm service `api` vào `docker-compose.yml` với `profiles: [full]`. Khai báo `build: { context: ., dockerfile: Dockerfile.runtime }` để dùng lại Dockerfile hiện tại mà không cần file mới.

2. Trước `docker compose --profile full build`: chạy `mvn clean package -DskipTests`, rồi copy JAR vào `app.jar` tại build context (root project) vì `Dockerfile.runtime` dùng `COPY app.jar app.jar`. Không dùng volume mount JAR — image build cần file trên host trước lệnh build.

3. Khai báo `env_file: .env` cho service `api` để load tất cả biến từ file `.env`. Đồng thời thêm `environment:` block với `DB_HOST: postgres` để override giá trị trong `.env` — đây là điểm mấu chốt để container tìm đúng service postgres thay vì `localhost`.

   **Cảnh báo:** Không volume-mount `.env` vào `/app/.env` trong container. `SportProBeApplication` load `.env` qua dotenv → `System.setProperty`, có thể ghi đè `DB_HOST=localhost` lên env của Compose và phá kết nối DB. Chỉ dùng `env_file:` + `environment:` override (không mount file).

4. Thêm `depends_on:` cho `api` trỏ tới `postgres` với `condition: service_healthy` — đảm bảo API không start trước khi DB sẵn sàng.

5. Expose port `8080:8080` cho service `api`. Healthcheck API: TCP `:8080` hoặc HTTP tới endpoint public (project không có Actuator).

## Success Criteria

- `docker compose --profile full up -d` khởi động cả `postgres` và `api` thành công
- `docker compose ps` hiển thị cả hai service đều `healthy` hoặc `running`
- `curl http://localhost:8080/api/...` (endpoint public, ví dụ products) trả về HTTP 2xx/401, không connection refused
- API kết nối được vào DB — không có lỗi `Connection refused` trong `docker compose logs api`
- `docker compose up -d` (không có `--profile full`) chỉ khởi động `postgres`, không khởi động `api`
- `Dockerfile.runtime` không bị sửa đổi; `deploy-vm.yml` không bị sửa đổi

## Risks

- Dev quên build JAR trước khi chạy `--profile full`: Ghi rõ bước này là điều kiện tiên quyết trong docs (Phase 3), thêm note trong compose file dưới dạng comment
- `DB_HOST=localhost` trong `.env` gây lỗi kết nối từ container: `environment:` override block trong compose đảm bảo `DB_HOST` luôn là `postgres` bất kể giá trị trong `.env`
- API start trước khi postgres ready dẫn đến crash-loop: `depends_on: condition: service_healthy` kết hợp với healthcheck ở Phase 1 ngăn điều này
