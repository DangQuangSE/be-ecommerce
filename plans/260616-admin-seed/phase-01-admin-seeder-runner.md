# Phase 1: Admin Seeder Runner

## Requirements

Tạo `AdminSeederRunner` trong module auth, chạy khi app startup, seed đúng 1 tài khoản admin nếu chưa tồn tại.

## Steps

1. Tạo file `src/main/java/com/sport_pro_be/modules/auth/service/AdminSeederRunner.java`:
   - `@Component`, `@Slf4j`, `@RequiredArgsConstructor`
   - Implement `CommandLineRunner`
   - Inject `UserRepository`, `PasswordEncoder`

2. Đọc config qua `@Value`:
   - `APP_ADMIN_SEED_ENABLED` — default `false`
   - `APP_ADMIN_EMAIL` — default empty
   - `APP_ADMIN_PASSWORD` — default empty

3. Logic `run()`:
   - Nếu `enabled` không phải `true` → log info, return
   - Nếu email hoặc password blank → log warn, return
   - Normalize email: `trim().toLowerCase()` (khớp `AuthService.normalizeEmail`)
   - Nếu `userRepository.existsByEmailIgnoreCase(email)` → log info "admin already exists", return
   - Tạo `User`:
     - `email` = normalized
     - `passwordHash` = `passwordEncoder.encode(password)`
     - `role` = `Role.ADMIN`
     - `emailVerified` = `true`
     - `isActive` = `true`
     - (các field khác dùng default entity: `tokenVersion`, `tier`, `totalSpending`)
   - `userRepository.save(user)` → log success (không log password)

4. Annotate `@Transactional` trên `run()` (giống `PrintingSeederRunner`).

## Files Changed

| File | Action |
|------|--------|
| `src/main/java/com/sport_pro_be/modules/auth/service/AdminSeederRunner.java` | Create |

## Success Criteria

- Compile thành công: `./mvnw compile -q`
- App start với seed enabled + credentials → log "Successfully seeded admin user"
- `SELECT email, role FROM app_users WHERE email = '<APP_ADMIN_EMAIL>'` → `ADMIN`
- Restart → log "Admin user already exists", count users không tăng

## Risks

- Email đã tồn tại với role `USER`: seeder skip — cần xóa user hoặc đổi email trong `.env` (document ở phase 2)
