-- Seed data for courses and dungeons
-- Replicating the structure from Water Ball Software Academy

-- Insert main course: 軟體設計模式 (Software Design Patterns)
INSERT INTO courses (course_id, title, description, cover_image_url, instructor_name, instructor_avatar_url, price, is_published, created_at, updated_at)
VALUES (
    1,
    '軟體設計模式',
    '透過實戰練習學習軟體設計模式，從基礎到進階，打造紮實的物件導向設計能力。包含8個主題副本，49個實戰影片，帶你從零到一建立完整的軟體設計思維。',
    'https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=800',
    'Water Ball老師',
    'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200',
    0.00,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert 8 dungeons (副本 0-7) for the course
-- Dungeon 0: 基礎篇
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES (
    1, 0, '副本0：物件導向基礎',
    '學習物件導向的基本概念，包括封裝、繼承、多型等核心原理。',
    1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Dungeon 1: SOLID原則
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES (
    1, 1, '副本1：SOLID設計原則',
    '深入理解SOLID五大設計原則，奠定良好的軟體設計基礎。',
    2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Dungeon 2: 創建型模式
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES (
    1, 2, '副本2：創建型設計模式',
    '學習Singleton、Factory、Builder等創建型模式的應用場景與實作技巧。',
    2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Dungeon 3: 結構型模式
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES (
    1, 3, '副本3：結構型設計模式',
    '掌握Adapter、Decorator、Facade等結構型模式，優化系統架構。',
    3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Dungeon 4: 行為型模式（上）
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES (
    1, 4, '副本4：行為型設計模式（上）',
    '學習Strategy、Observer、Command等行為型模式，提升程式碼的彈性。',
    3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Dungeon 5: 行為型模式（下）
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES (
    1, 5, '副本5：行為型設計模式（下）',
    '深入State、Template Method、Iterator等進階行為型模式。',
    4, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Dungeon 6: 架構模式
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES (
    1, 6, '副本6：架構設計模式',
    '學習MVC、MVVM、Clean Architecture等架構模式的設計思維。',
    4, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Dungeon 7: 實戰專案
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES (
    1, 7, '副本7：綜合實戰專案',
    '整合所有學習過的設計模式，完成一個完整的實戰專案。',
    4, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Insert sample videos for Dungeon 0 (first 6 videos as examples)
INSERT INTO videos (title, description, duration_seconds, video_url, thumbnail_url, course_id, dungeon_id, chapter_number, order_index, exp_reward, created_at, updated_at)
VALUES
    ('物件導向概論', '介紹物件導向程式設計的核心概念與優勢', 1200, 'https://example.com/video1.mp4', 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 0), 0, 0, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('封裝（Encapsulation）', '學習如何使用封裝來保護物件的內部狀態', 900, 'https://example.com/video2.mp4', 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 0), 0, 1, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('繼承（Inheritance）', '理解繼承的概念與正確的使用時機', 1000, 'https://example.com/video3.mp4', 'https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 0), 0, 2, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('多型（Polymorphism）', '掌握多型的威力，寫出更有彈性的程式碼', 1100, 'https://example.com/video4.mp4', 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 0), 0, 3, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('抽象（Abstraction）', '學習如何運用抽象來簡化複雜的系統', 950, 'https://example.com/video5.mp4', 'https://images.unsplash.com/photo-1487058792275-0ad4aaf24ca7?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 0), 0, 4, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('介面 vs 抽象類別', '深入比較介面與抽象類別的差異與應用場景', 1050, 'https://example.com/video6.mp4', 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 0), 0, 5, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample videos for Dungeon 1
INSERT INTO videos (title, description, duration_seconds, video_url, thumbnail_url, course_id, dungeon_id, chapter_number, order_index, exp_reward, created_at, updated_at)
VALUES
    ('單一職責原則 (SRP)', '一個類別應該只有一個改變的理由', 800, 'https://example.com/video7.mp4', 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 1), 1, 0, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('開放封閉原則 (OCP)', '軟體實體應該對擴展開放，對修改封閉', 850, 'https://example.com/video8.mp4', 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 1), 1, 1, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('里氏替換原則 (LSP)', '子類別應該能夠替換其父類別', 900, 'https://example.com/video9.mp4', 'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?w=400', 1, (SELECT dungeon_id FROM dungeons WHERE course_id = 1 AND dungeon_number = 1), 1, 2, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add a second course (free course)
INSERT INTO courses (course_id, title, description, cover_image_url, instructor_name, instructor_avatar_url, price, is_published, created_at, updated_at)
VALUES (
    2,
    'Git 版本控制入門',
    '從零開始學習Git版本控制系統，掌握團隊協作的必備技能。包含基礎指令、分支管理、衝突解決等實用技巧。',
    'https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=800',
    'DevOps團隊',
    'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200',
    0.00,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Add dungeons for Git course
INSERT INTO dungeons (course_id, dungeon_number, title, description, difficulty, order_index, created_at, updated_at)
VALUES
    (2, 0, 'Git基礎操作', '學習Git的基本指令與工作流程', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, '分支管理', '掌握Git分支的創建、合併與管理技巧', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add sample videos for Git course
INSERT INTO videos (title, description, duration_seconds, video_url, thumbnail_url, course_id, dungeon_id, chapter_number, order_index, exp_reward, created_at, updated_at)
VALUES
    ('Git簡介與安裝', 'Git版本控制系統簡介與環境設定', 600, 'https://example.com/git1.mp4', 'https://images.unsplash.com/photo-1556075798-4825dfaaf498?w=400', 2, (SELECT dungeon_id FROM dungeons WHERE course_id = 2 AND dungeon_number = 0), 0, 0, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('基本指令操作', '學習git add, commit, push等基本指令', 720, 'https://example.com/git2.mp4', 'https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?w=400', 2, (SELECT dungeon_id FROM dungeons WHERE course_id = 2 AND dungeon_number = 0), 0, 1, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add comments
COMMENT ON COLUMN courses.course_id IS 'Primary key for courses (manually set for seed data)';
COMMENT ON COLUMN dungeons.dungeon_number IS 'Dungeon number 0-7 for main course dungeons';
