INSERT IGNORE INTO users (id, username, email, password, avatar, bio, role, status)
VALUES
    (1, 'admin', 'admin@webtruyen.local', '$2a$10$9WBsM8fJQzv6W8w6bb7bSew5f6REhWQ0YB8F39pW8qMXzYwK4lMM6', NULL, 'System admin', 'ADMIN', 'ACTIVE'),
    (2, 'author_anna', 'anna@webtruyen.local', '$2a$10$9WBsM8fJQzv6W8w6bb7bSew5f6REhWQ0YB8F39pW8qMXzYwK4lMM6', NULL, 'Tac gia the loai tien hiep', 'USER', 'ACTIVE'),
    (3, 'author_hieu', 'hieu@webtruyen.local', '$2a$10$9WBsM8fJQzv6W8w6bb7bSew5f6REhWQ0YB8F39pW8qMXzYwK4lMM6', NULL, 'Tac gia do thi', 'USER', 'ACTIVE'),
    (10, 'postman_user', 'postman_user@webtruyen.local', '123456', NULL, 'Tai khoan test login Postman', 'USER', 'ACTIVE'),
    (11, 'postman_admin', 'postman_admin@webtruyen.local', '123456', NULL, 'Tai khoan test role ADMIN cho frontend redirect', 'ADMIN', 'ACTIVE');

INSERT IGNORE INTO genres (id, name)
VALUES
    (1, 'Tien Hiep'),
    (2, 'Do Thi'),
    (3, 'Huyen Huyen'),
    (4, 'He Thong');

INSERT IGNORE INTO truyen (id, title, slug, description, cover_image, author_id, author_name, status, publish_status, view_count, approved_at)
VALUES
    (1, 'Nhhat Kiem Van Co', 'nhat-kiem-van-co', 'Mot thanh nien xuyen khong vao the gioi tu tien, tung buoc truong thanh thanh kiem ton.', 'https://images.example.com/truyen/nhat-kiem-van-co.jpg', 2, 'author_anna', 'APPROVED', 'ONGOING', 12540, NOW()),
    (2, 'Do Thi Chi Ton', 'do-thi-chi-ton', 'Tu chan tro lai hien dai, han mang theo cong phap va bi mat vo dao.', 'https://images.example.com/truyen/do-thi-chi-ton.jpg', 3, 'author_hieu', 'APPROVED', 'ONGOING', 8730, NOW()),
    (3, 'Than Cap He Thong', 'than-cap-he-thong', 'Nguoi choi kich hoat he thong va chinh phuc hang van pho ban nguy hiem.', 'https://images.example.com/truyen/than-cap-he-thong.jpg', 2, 'author_anna', 'PENDING', 'ONGOING', 920, NULL),
    (4, 'Linh Vuc Bat Diet', 'linh-vuc-bat-diet', 'Hanh trinh tim lai ky uc trong linh vuc vo tan.', 'https://images.example.com/truyen/linh-vuc-bat-diet.jpg', 3, 'author_hieu', 'APPROVED', 'COMPLETED', 20110, NOW());

INSERT IGNORE INTO truyen_genres (truyen_id, genre_id)
VALUES
    (1, 1),
    (1, 3),
    (2, 2),
    (3, 4),
    (4, 1),
    (4, 3);

INSERT IGNORE INTO chapters (id, truyen_id, chapter_number, title, content, view_count)
VALUES
    (1, 1, 1, 'Chuong 1: Kiem y noi day', 'Noi dung chuong 1...', 3200),
    (2, 1, 2, 'Chuong 2: Tu luyen bat dau', 'Noi dung chuong 2...', 2900),
    (3, 2, 1, 'Chuong 1: Tro lai thanh pho', 'Noi dung chuong 1...', 1800),
    (4, 4, 1, 'Chuong 1: Linh vuc mo ra', 'Noi dung chuong 1...', 4100);

INSERT IGNORE INTO comments (id, user_id, truyen_id, chapter_id, content)
VALUES
    (1, 1, 1, 1, 'Mo dau rat cuon.'),
    (2, 1, 2, NULL, 'Mong tac gia ra chuong deu.');

INSERT IGNORE INTO favorites (user_id, truyen_id)
VALUES
    (1, 1),
    (1, 2);

INSERT IGNORE INTO ratings (user_id, truyen_id, rating)
VALUES
    (1, 1, 5),
    (1, 2, 4);
