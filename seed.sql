-- SQL Seeding Script for Sport Pro Database
-- Make sure the Spring Boot app has run once to generate the schema, or run this after tables are created.

-- 1. Seed Users (passwords are 'Password123' hashed with BCrypt)
-- Hash: $2a$10$kQkfdBL0nnV0CTzwWeRNkOtmPAJRp8T5kf07rKas0puD4K5XtnzyC
INSERT INTO app_users (email, first_name, last_name, avatar, password_hash, email_verified, role, token_version, total_spending, tier, created_at, updated_at)
VALUES 
('admin@sportpro.com', 'Admin', 'SportPro', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e', '$2a$10$kQkfdBL0nnV0CTzwWeRNkOtmPAJRp8T5kf07rKas0puD4K5XtnzyC', true, 'ADMIN', 1, 0.00, 'GOLD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user@sportpro.com', 'John', 'Doe', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde', '$2a$10$kQkfdBL0nnV0CTzwWeRNkOtmPAJRp8T5kf07rKas0puD4K5XtnzyC', true, 'USER', 1, 0.00, 'BRONZE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- 2. Seed Categories
INSERT INTO categories (name, slug, description, image_url, is_active, is_customizable, display_order, created_at, updated_at)
VALUES 
('Giày Chạy Bộ', 'giay-chay-bo', 'Các dòng giày chạy bộ chuyên nghiệp chính hãng, siêu nhẹ và êm chân', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', true, false, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Trang Phục Thể Thao', 'trang-phuc-the-thao', 'Áo thun, quần thun co giãn chuyên dụng cho tập luyện thể thao', 'https://images.unsplash.com/photo-1506152983158-b4a74a01c721', true, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (slug) DO NOTHING;

-- 3. Seed Brands
INSERT INTO brands (name, slug, description, logo_url, country, website_url, is_active, created_at, updated_at)
VALUES 
('Nike', 'nike', 'Thương hiệu thể thao hàng đầu thế giới từ Mỹ', 'https://upload.wikimedia.org/wikipedia/commons/a/a6/Logo_NIKE.svg', 'USA', 'https://www.nike.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Adidas', 'adidas', 'Thương hiệu thể thao cao cấp lâu đời từ Đức', 'https://upload.wikimedia.org/wikipedia/commons/2/20/Adidas_Logo.svg', 'Germany', 'https://www.adidas.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Puma', 'puma', 'Thương hiệu thể thao năng động trẻ trung', 'https://upload.wikimedia.org/wikipedia/commons/8/88/Puma_Logo.svg', 'Germany', 'https://www.puma.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (slug) DO NOTHING;

-- 4. Seed Colors (Product Colors)
INSERT INTO colors (name, hex_code, created_at, updated_at)
VALUES 
('Red Blast', '#FF1E27', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cobalt Blue', '#1E90FF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Lime Green', '#32CD32', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Neon Yellow', '#E0FF00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Core Black', '#000000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cloud White', '#FFFFFF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- 5. Seed Products
INSERT INTO products (name, slug, description, category_id, brand_id, gender, status, average_rating, review_count, is_featured, created_at, updated_at)
VALUES 
('AeroGlide Pro Runner X1', 'aeroglide-pro-runner-x1', 'Giày chạy bộ chuyên nghiệp siêu nhẹ khí động học với đệm carbon phản hồi lực tối đa.', 
 (SELECT id FROM categories WHERE slug = 'giay-chay-bo' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'nike' LIMIT 1), 'UNISEX', 'ACTIVE', 4.8, 12, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Phantom Speed Knit 2.0', 'phantom-speed-knit-2-0', 'Giày chạy bộ dệt kim với độ đàn hồi, thoáng khí và hỗ trợ bám đường vượt trội.', 
 (SELECT id FROM categories WHERE slug = 'giay-chay-bo' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'adidas' LIMIT 1), 'MALE', 'ACTIVE', 4.5, 8, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('AeroTech Tee', 'aerotech-tee', 'Áo thun thể thao chuyên dụng AeroTech siêu nhẹ, hỗ trợ tùy chỉnh thiết kế riêng biệt và công nghệ Dry-Fit.', 
 (SELECT id FROM categories WHERE slug = 'trang-phuc-the-thao' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'nike' LIMIT 1), 'UNISEX', 'ACTIVE', 4.9, 25, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (slug) DO NOTHING;

-- 6. Seed Product Variants
INSERT INTO product_variants (product_id, sku, size, color, color_id, original_price, sale_price, stock_quantity, status, created_at, updated_at)
VALUES 
-- AeroGlide Pro Runner X1 Variants
((SELECT id FROM products WHERE slug = 'aeroglide-pro-runner-x1' LIMIT 1), 'NIKE-AG-40-RED-US9', 'US 9', 'Red Blast', (SELECT id FROM colors WHERE name = 'Red Blast' LIMIT 1), 2450000.00, 2450000.00, 20, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE slug = 'aeroglide-pro-runner-x1' LIMIT 1), 'NIKE-AG-40-BLUE-US9.5', 'US 9.5', 'Cobalt Blue', (SELECT id FROM colors WHERE name = 'Cobalt Blue' LIMIT 1), 2450000.00, 2200000.00, 15, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE slug = 'aeroglide-pro-runner-x1' LIMIT 1), 'NIKE-AG-40-BLK-US10', 'US 10', 'Core Black', (SELECT id FROM colors WHERE name = 'Core Black' LIMIT 1), 2450000.00, 2450000.00, 30, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Phantom Speed Knit 2.0 Variants
((SELECT id FROM products WHERE slug = 'phantom-speed-knit-2-0' LIMIT 1), 'ADI-PSK-WHT-US8.5', 'US 8.5', 'Cloud White', (SELECT id FROM colors WHERE name = 'Cloud White' LIMIT 1), 3100000.00, 3100000.00, 10, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE slug = 'phantom-speed-knit-2-0' LIMIT 1), 'ADI-PSK-BLK-US9', 'US 9', 'Core Black', (SELECT id FROM colors WHERE name = 'Core Black' LIMIT 1), 3100000.00, 2800000.00, 25, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- AeroTech Tee Variants
((SELECT id FROM products WHERE slug = 'aerotech-tee' LIMIT 1), 'NIKE-AT-TEE-WHT-M', 'M', 'Cloud White', (SELECT id FROM colors WHERE name = 'Cloud White' LIMIT 1), 450000.00, 450000.00, 50, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE slug = 'aerotech-tee' LIMIT 1), 'NIKE-AT-TEE-RED-L', 'L', 'Red Blast', (SELECT id FROM colors WHERE name = 'Red Blast' LIMIT 1), 450000.00, 450000.00, 40, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (sku) DO NOTHING;

-- 7. Seed Product Images
INSERT INTO product_images (product_id, variant_id, image_url, is_thumbnail, sort_order, created_at, updated_at)
VALUES 
-- AeroGlide Pro Runner X1 Images
((SELECT id FROM products WHERE slug = 'aeroglide-pro-runner-x1' LIMIT 1), null, 'https://lh3.googleusercontent.com/aida-public/AB6AXuBhgk4WkWkBXTRrUCXJt6iovhM2-wJzKcWFp1HBGJ2pLpBOe93MIfa1XOL3XXbHcUvA-_F6JDtc6pM4Yk8dgBWZJ4hLbRYaka6u6IAMHjJ2V9u6-0hiXYyDoYj-IVyv5B0PsvZcGg4nG520HHbRjayi4JywFFl2EM6aBawUw96VQlMYLM7fWrMPC2RwlOeugRegU1rzkI4GMgE1vtZPCB60wlUc1THtKIdvVC4NYM5yWvLydWxWuzZpnLsPvE7pcGnWdB0lYk4KQz0', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Phantom Speed Knit 2.0 Images
((SELECT id FROM products WHERE slug = 'phantom-speed-knit-2-0' LIMIT 1), null, 'https://lh3.googleusercontent.com/aida-public/AB6AXuAnZxEtabjD9PjXesR4AEiZFB7Wi46_Xs-9s5BN5GySN0Bs5BBVLEf_WkgEaKB01390Ld59KnH9OUuOc8GaI5BWOlXaTmPapDDdR1cUlF5hHQRKT7LhOGZClfxfEZLuQhYT7IPYNB-HwkEdI5Lkio1BlwNJCwgxunBpnTYjHpG3kbd0rfqGvcREjnZu8SfL7-RJhBRgMaUWiMSfAMXbAisJ6vQo0KXJ5ApfF7hzPn_fYfhUTmiM4g7lyQFV22VHKY9BunMDQyVp3D0', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- AeroTech Tee Images
((SELECT id FROM products WHERE slug = 'aerotech-tee' LIMIT 1), null, 'https://lh3.googleusercontent.com/aida-public/AB6AXuAg16llodl6Hl8MPqH6DvSysphHsH9azINDafCIQFp9rqCHyIEj5IyNuBfAVIK7-s1m70zLJYYuRDn7ps4e9BkxeY1wfIJ58BidKV1GgULrOntZ7svsuNpwj8nvPhazvHISS-5OqI81qGvWmbwLlQlDr7PaeNVO1DpmYgljTca2s33rrrPqLBq7MLlaEkQdj7fqz_fN5K-XrOluv8Ux-V0w9V8-aE1C5t5BlJtTl7b0-7Tot4btl19oWsO5WWVz6wdqu1TcpvcIJ6k', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =========================================================================
-- ADDITIONAL PRODUCTS SEED DATA (Added for testing)
-- =========================================================================

-- 8. Seed More Products
INSERT INTO products (name, slug, description, category_id, brand_id, gender, status, average_rating, review_count, is_featured, created_at, updated_at)
VALUES 
('Nike Pegasus 40', 'nike-pegasus-40', 'Dòng giày chạy bộ huyền thoại của Nike với đệm React và hai túi đệm Zoom Air giúp phản hồi lực cực tốt.', 
 (SELECT id FROM categories WHERE slug = 'giay-chay-bo' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'nike' LIMIT 1), 'UNISEX', 'ACTIVE', 4.7, 15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Adidas Ultraboost Light', 'adidas-ultraboost-light', 'Thế hệ Ultraboost mới siêu nhẹ, mang lại sự êm ái tối đa cho bàn chân trên mọi cung đường.', 
 (SELECT id FROM categories WHERE slug = 'giay-chay-bo' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'adidas' LIMIT 1), 'UNISEX', 'ACTIVE', 4.6, 9, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Puma Deviate Nitro 2', 'puma-deviate-nitro-2', 'Giày chạy bộ có đệm carbon cao cấp nhất của Puma, tối ưu tốc độ và phản hồi năng lượng vượt trội.', 
 (SELECT id FROM categories WHERE slug = 'giay-chay-bo' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'puma' LIMIT 1), 'MALE', 'ACTIVE', 4.8, 7, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Nike Dri-FIT Challenger Shorts', 'nike-dri-fit-challenger-shorts', 'Quần short chạy bộ thoáng khí, thấm hút mồ hôi tốt, thiết kế năng động và hỗ trợ vận động tối đa.', 
 (SELECT id FROM categories WHERE slug = 'trang-phuc-the-thao' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'nike' LIMIT 1), 'MALE', 'ACTIVE', 4.5, 18, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Adidas Own The Run Tee', 'adidas-own-the-run-tee', 'Áo thun chạy bộ Adidas làm từ chất liệu tái chế thân thiện môi trường, phản quang trong bóng tối.', 
 (SELECT id FROM categories WHERE slug = 'trang-phuc-the-thao' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'adidas' LIMIT 1), 'UNISEX', 'ACTIVE', 4.4, 11, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Puma Run Favorite Singlet', 'puma-run-favorite-singlet', 'Áo ba lỗ thể thao siêu thoáng khí, thiết kế ergonomic giúp thoải mái tuyệt đối khi tập luyện cường độ cao.', 
 (SELECT id FROM categories WHERE slug = 'trang-phuc-the-thao' LIMIT 1), 
 (SELECT id FROM brands WHERE slug = 'puma' LIMIT 1), 'MALE', 'ACTIVE', 4.6, 6, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (slug) DO NOTHING;

-- 9. Seed More Product Variants
INSERT INTO product_variants (product_id, sku, size, color, color_id, original_price, sale_price, stock_quantity, status, created_at, updated_at)
VALUES
-- Nike Pegasus 40
((SELECT id FROM products WHERE slug = 'nike-pegasus-40' LIMIT 1), 'NIKE-PEG40-BLK-US9', 'US 9', 'Core Black', (SELECT id FROM colors WHERE name = 'Core Black' LIMIT 1), 3500000.00, 3500000.00, 25, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE slug = 'nike-pegasus-40' LIMIT 1), 'NIKE-PEG40-WHT-US9.5', 'US 9.5', 'Cloud White', (SELECT id FROM colors WHERE name = 'Cloud White' LIMIT 1), 3500000.00, 3200000.00, 20, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Adidas Ultraboost Light
((SELECT id FROM products WHERE slug = 'adidas-ultraboost-light' LIMIT 1), 'ADI-UB-LIGHT-WHT-US9', 'US 9', 'Cloud White', (SELECT id FROM colors WHERE name = 'Cloud White' LIMIT 1), 4800000.00, 4800000.00, 15, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE slug = 'adidas-ultraboost-light' LIMIT 1), 'ADI-UB-LIGHT-BLK-US10', 'US 10', 'Core Black', (SELECT id FROM colors WHERE name = 'Core Black' LIMIT 1), 4800000.00, 4300000.00, 22, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Puma Deviate Nitro 2
((SELECT id FROM products WHERE slug = 'puma-deviate-nitro-2' LIMIT 1), 'PUMA-DN2-RED-US9.5', 'US 9.5', 'Red Blast', (SELECT id FROM colors WHERE name = 'Red Blast' LIMIT 1), 4200000.00, 4200000.00, 12, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Nike Dri-FIT Challenger Shorts
((SELECT id FROM products WHERE slug = 'nike-dri-fit-challenger-shorts' LIMIT 1), 'NIKE-DRF-SHORTS-BLK-M', 'M', 'Core Black', (SELECT id FROM colors WHERE name = 'Core Black' LIMIT 1), 750000.00, 750000.00, 40, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM products WHERE slug = 'nike-dri-fit-challenger-shorts' LIMIT 1), 'NIKE-DRF-SHORTS-BLU-L', 'L', 'Cobalt Blue', (SELECT id FROM colors WHERE name = 'Cobalt Blue' LIMIT 1), 750000.00, 690000.00, 35, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Adidas Own The Run Tee
((SELECT id FROM products WHERE slug = 'adidas-own-the-run-tee' LIMIT 1), 'ADI-OTR-TEE-YEL-M', 'M', 'Neon Yellow', (SELECT id FROM colors WHERE name = 'Neon Yellow' LIMIT 1), 850000.00, 850000.00, 30, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Puma Run Favorite Singlet
((SELECT id FROM products WHERE slug = 'puma-run-favorite-singlet' LIMIT 1), 'PUMA-RFS-SING-GRN-L', 'L', 'Lime Green', (SELECT id FROM colors WHERE name = 'Lime Green' LIMIT 1), 650000.00, 650000.00, 25, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (sku) DO NOTHING;

-- 10. Seed More Product Images
INSERT INTO product_images (product_id, variant_id, image_url, is_thumbnail, sort_order, created_at, updated_at)
SELECT (SELECT id FROM products WHERE slug = 'nike-pegasus-40' LIMIT 1), null, 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?auto=format&fit=crop&w=600&q=80', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product_images WHERE image_url = 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?auto=format&fit=crop&w=600&q=80');

INSERT INTO product_images (product_id, variant_id, image_url, is_thumbnail, sort_order, created_at, updated_at)
SELECT (SELECT id FROM products WHERE slug = 'adidas-ultraboost-light' LIMIT 1), null, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=600&q=80', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product_images WHERE image_url = 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?auto=format&fit=crop&w=600&q=80');

INSERT INTO product_images (product_id, variant_id, image_url, is_thumbnail, sort_order, created_at, updated_at)
SELECT (SELECT id FROM products WHERE slug = 'puma-deviate-nitro-2' LIMIT 1), null, 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=600&q=80', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product_images WHERE image_url = 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?auto=format&fit=crop&w=600&q=80');

INSERT INTO product_images (product_id, variant_id, image_url, is_thumbnail, sort_order, created_at, updated_at)
SELECT (SELECT id FROM products WHERE slug = 'nike-dri-fit-challenger-shorts' LIMIT 1), null, 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?auto=format&fit=crop&w=600&q=80', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product_images WHERE image_url = 'https://images.unsplash.com/photo-1591195853828-11db59a44f6b?auto=format&fit=crop&w=600&q=80');

INSERT INTO product_images (product_id, variant_id, image_url, is_thumbnail, sort_order, created_at, updated_at)
SELECT (SELECT id FROM products WHERE slug = 'adidas-own-the-run-tee' LIMIT 1), null, 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?auto=format&fit=crop&w=600&q=80', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product_images WHERE image_url = 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?auto=format&fit=crop&w=600&q=80');

INSERT INTO product_images (product_id, variant_id, image_url, is_thumbnail, sort_order, created_at, updated_at)
SELECT (SELECT id FROM products WHERE slug = 'puma-run-favorite-singlet' LIMIT 1), null, 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=600&q=80', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM product_images WHERE image_url = 'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=600&q=80');
