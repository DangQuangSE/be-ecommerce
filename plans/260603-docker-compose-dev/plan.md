# Plan: Docker Compose cho Local Development

Status: 🟡 In Progress  
Date: 2026-06-03  
Mode: Hard  
Testing: Manual smoke test (không yêu cầu automated test suite mới)

## Overview

Thêm `docker-compose.yml` để chạy PostgreSQL và Spring Boot API trên máy local bằng một lệnh duy nhất, không ảnh hưởng đến quy trình CI/CD hiện tại (`deploy-vm.yml` + `Dockerfile.runtime`).

## Phases

- [ ] Phase 1: PostgreSQL Service — Cấu hình postgres service, volume, healthcheck, và căn chỉnh với `.env.example`
- [ ] Phase 2: API Service với Compose Profiles — Thêm `api` service dùng `Dockerfile.runtime` dưới profile `full`, wiring env qua `.env`
- [ ] Phase 3: Tài liệu hướng dẫn — Viết `docs/docker-compose.md` với hướng dẫn cài đặt, lệnh sử dụng, troubleshooting
- [ ] Phase 4: Smoke Validation — Xác nhận toàn bộ stack chạy đúng trên môi trường local

## Research Summary

Hai hướng tiếp cận đã được phân tích:

**Hướng A — Multi-stage Dockerfile mới:** Tạo Dockerfile build-from-source để compose tự build JAR. Phù hợp với clone-and-run workflow nhưng build chậm trên Windows và cần thêm Dockerfile mới vào repo.

**Hướng B — Compose Profiles + Dockerfile.runtime:** (đã thay bằng quyết định user)

**Hướng C — Multi-stage Dockerfile + full stack (CHỌN theo user 2026-06-03):**
- Luôn chạy cả API + DB: `docker compose up` (không profile)
- Service DB: `database_postgre`, port `5432:5432`
- SMTP thật từ `.env`
- Multi-stage `Dockerfile` — một lệnh build + run

~~Hướng B cũ:~~ Profile `full` + `Dockerfile.runtime`. Cách cũ:
- Căn chỉnh hoàn toàn với `deploy-vm.yml` — không tạo phân kỳ giữa local và production
- Không cần build Maven bên trong Docker (dev tự build JAR bằng IDE hoặc `mvn package`)
- Nhanh hơn trên Windows (không cần multi-stage)
- Tách biệt rõ ràng: chỉ cần DB thì dùng default; cần full stack thì thêm `--profile full`

## Dependencies

- Docker Desktop >= 4.x với Compose v2 (`docker compose` không phải `docker-compose`)
- JAR đã được build sẵn tại `target/*.jar` trước khi chạy profile `full`
- File `.env` tạo từ `.env.example` (giữ `DB_HOST=localhost` cho dev IDE; compose override `DB_HOST=postgres` cho container API)

## Risks

- HIGH: Dev quên đổi `DB_HOST=postgres` trong `.env` khi chạy trong container — Mitigation: Compose file override `DB_HOST` bằng `environment:` block, bất kể giá trị trong `.env`
- MEDIUM: JAR chưa được build trước khi chạy `--profile full` — Mitigation: Ghi rõ trong tài liệu, thêm bước kiểm tra `target/` trong smoke test
- MEDIUM: Port conflict 5432 nếu PostgreSQL local đang chạy trên máy — Mitigation: Hướng dẫn đổi host port trong docs (ví dụ `5433:5432`)
- LOW: `Dockerfile.runtime` expect `app.jar` ở working directory — Mitigation: Compose volume mount hoặc build context trỏ đúng, ghi rõ trong phase 2
- LOW: CI pipeline vô tình dùng `docker-compose.yml` mới — Mitigation: `deploy-vm.yml` không tham chiếu file này; verify trong phase 4

## Risks (from plan-reviewer)

- NOTED: `MAIL_USERNAME` không có trong `SportProBeApplication` dotenv loader — `env_file` của Compose vẫn inject qua OS env (đủ cho SMTP).
- NOTED: Không có Spring Actuator — smoke test dùng endpoint public (ví dụ `/api/products`) thay vì `/actuator/health`.
