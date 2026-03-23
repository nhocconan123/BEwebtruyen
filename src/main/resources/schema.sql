CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    bio TEXT,
    role ENUM('USER','ADMIN') DEFAULT 'USER',
    status ENUM('ACTIVE','BANNED') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS genres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS user_genres (
    user_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (user_id, genre_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS truyen (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    description TEXT,
    cover_image VARCHAR(255),
    author_id BIGINT,
    author_name VARCHAR(255),
    status ENUM('DRAFT','PENDING','APPROVED','REJECTED') DEFAULT 'DRAFT',
    publish_status ENUM('ONGOING','COMPLETED','PAUSED') DEFAULT 'ONGOING',
    view_count BIGINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    approved_at DATETIME,
    FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS truyen_genres (
    truyen_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (truyen_id, genre_id),
    FOREIGN KEY (truyen_id) REFERENCES truyen(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS chapters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    truyen_id BIGINT,
    chapter_number INT,
    title VARCHAR(255),
    content LONGTEXT,
    view_count BIGINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (truyen_id) REFERENCES truyen(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    truyen_id BIGINT,
    chapter_id BIGINT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (truyen_id) REFERENCES truyen(id),
    FOREIGN KEY (chapter_id) REFERENCES chapters(id)
);

CREATE TABLE IF NOT EXISTS favorites (
    user_id BIGINT,
    truyen_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, truyen_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (truyen_id) REFERENCES truyen(id)
);

CREATE TABLE IF NOT EXISTS follows (
    follower_id BIGINT,
    following_id BIGINT,
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id),
    FOREIGN KEY (following_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS ratings (
    user_id BIGINT,
    truyen_id BIGINT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    PRIMARY KEY (user_id, truyen_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (truyen_id) REFERENCES truyen(id)
);

CREATE TABLE IF NOT EXISTS reading_history (
    user_id BIGINT,
    chapter_id BIGINT,
    last_read_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, chapter_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (chapter_id) REFERENCES chapters(id)
);

-- Patch for existing databases: add missing columns and backfill values.
-- spring.sql.init.continue-on-error=true so re-running is safe (may log harmless "duplicate column" errors).
ALTER TABLE truyen ADD COLUMN author_name VARCHAR(255);
UPDATE truyen t
JOIN users u ON u.id = t.author_id
SET t.author_name = COALESCE(t.author_name, u.username)
WHERE t.author_name IS NULL;
