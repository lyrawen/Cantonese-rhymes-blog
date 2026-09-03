import re
import pathlib

src = pathlib.Path(r"d:\粤韵志\插入数据.md").read_text(encoding="utf-8")
src = src.replace("NVARCHAR(MAX)", "LONGTEXT")
src = re.sub(r"NVARCHAR\((\d+)\)", r"VARCHAR(\1)", src)
src = src.replace(
    "CREATE DATABASE YueYunZhiDB;",
    "DROP DATABASE IF EXISTS yueyunzhi;\n"
    "CREATE DATABASE yueyunzhi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;",
)
src = src.replace(
    "USE YueYunZhiDB;",
    "USE yueyunzhi;\nSET NAMES utf8mb4;\nSET FOREIGN_KEY_CHECKS = 0;",
)

if not src.rstrip().endswith(";"):
    src = src.rstrip()
    src = re.sub(
        r"\(5, 5, 2, '2025-01-17 09:15:.*$",
        "(5, 5, 2, '2025-01-17 09:15:00'),\n"
        "(6, 1, 2, '2025-01-17 09:15:00'),\n"
        "(6, 3, 1, '2025-01-16 10:00:00'),\n"
        "(7, 1, 3, '2025-01-19 18:00:00'),\n"
        "(8, 5, 2, '2025-01-18 12:00:00'),\n"
        "(9, 2, 1, '2025-01-17 14:00:00'),\n"
        "(10, 8, 4, '2025-01-20 09:00:00');",
        src,
        flags=re.S,
    )

header = """-- =====================================================
-- 粤韵志 MySQL 初始化脚本（XAMPP）
-- 用法：在 phpMyAdmin 或 mysql 命令行中执行本文件
-- 默认连接：localhost:3306  用户 root  密码（空）
-- 测试登录密码：123456（所有用户）
-- =====================================================

"""

src = src.replace(
    "    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'\n) ENGINE=InnoDB",
    "    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',\n"
    "    cover_photo VARCHAR(255) COMMENT '个人主页封面'\n) ENGINE=InnoDB",
)

new_articles = """CREATE TABLE articles (
    article_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID',
    author_id BIGINT NOT NULL COMMENT '作者',
    category_id INT COMMENT '分类',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    summary VARCHAR(500) COMMENT '摘要',
    excerpt VARCHAR(500) COMMENT '摘录',
    content LONGTEXT NOT NULL COMMENT '文章内容',
    cover_image VARCHAR(255) COMMENT '封面图',
    status INT DEFAULT 0 COMMENT '0待审核 1已发布 2已拒绝',
    is_featured TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
    view_count INT DEFAULT 0 COMMENT '阅读量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    favorite_count INT DEFAULT 0 COMMENT '收藏数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    publish_time DATETIME COMMENT '发布时间',
    updated_at DATETIME COMMENT '更新时间冗余',
    author_name VARCHAR(50) COMMENT '作者名冗余',
    author_avatar VARCHAR(200) COMMENT '作者头像冗余',
    category VARCHAR(100) COMMENT '分类名冗余',
    tags VARCHAR(255) COMMENT '标签冗余',
    FOREIGN KEY (author_id) REFERENCES users(user_id),
    FOREIGN KEY (category_id) REFERENCES article_category(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';"""

src = re.sub(
    r"CREATE TABLE articles \(.*?COMMENT='文章表';",
    new_articles,
    src,
    flags=re.S,
)

new_comments = """CREATE TABLE comments (
    comment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    article_id BIGINT NOT NULL COMMENT '文章',
    user_id BIGINT NOT NULL COMMENT '评论用户',
    user_name VARCHAR(100) COMMENT '评论者昵称',
    user_avatar VARCHAR(200) COMMENT '评论者头像',
    content LONGTEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT COMMENT '父评论',
    reply_to_user_id BIGINT COMMENT '被回复用户',
    reply_to_user_name VARCHAR(100) COMMENT '被回复用户昵称',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '1正常 0屏蔽',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (article_id) REFERENCES articles(article_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (parent_id) REFERENCES comments(comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';"""

src = re.sub(
    r"CREATE TABLE comments \(.*?COMMENT='评论表';",
    new_comments,
    src,
    flags=re.S,
)

extra = """

-- =====================================================
-- 10. comment_like（评论点赞表）
-- =====================================================
CREATE TABLE comment_like (
    user_id BIGINT NOT NULL COMMENT '点赞用户',
    comment_id BIGINT NOT NULL COMMENT '被点赞评论',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (user_id, comment_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (comment_id) REFERENCES comments(comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

INSERT INTO comment_like (user_id, comment_id) VALUES
(2, 1), (3, 1), (4, 1), (5, 1), (1, 4), (6, 6), (7, 7), (8, 9);

-- =====================================================
-- 11. user_follow（用户关注表）
-- =====================================================
CREATE TABLE user_follow (
    follower_id BIGINT NOT NULL COMMENT '粉丝',
    following_id BIGINT NOT NULL COMMENT '被关注者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(user_id),
    FOREIGN KEY (following_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

INSERT INTO user_follow (follower_id, following_id) VALUES
(2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1),
(1, 2), (3, 2), (4, 2), (1, 3), (2, 3), (1, 5), (2, 5), (3, 5),
(10, 1), (11, 2), (12, 3), (13, 4);

-- =====================================================
-- 12. ai_chat_history（AI 问答记录表）
-- =====================================================
CREATE TABLE ai_chat_history (
    chat_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户',
    question LONGTEXT COMMENT '问题',
    answer LONGTEXT COMMENT '回答',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI问答记录表';

INSERT INTO ai_chat_history (user_id, question, answer) VALUES
(1, '粤剧起源于什么时候？', '粤剧起源于明代，成熟于清代，是岭南最具代表性的戏曲艺术形式之一。'),
(3, '「打边炉」是什么意思？', '「打边炉」是粤语中对火锅的称呼，源于围炉而食、边涮边聊的饮食方式。');

-- =====================================================
-- 13. announcements（公告表）
-- =====================================================
CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content LONGTEXT COMMENT '内容',
    status TINYINT DEFAULT 1 COMMENT '1发布 0下线',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

INSERT INTO announcements (title, content, status) VALUES
('欢迎来到粤韵志', '粤韵志是一个专注岭南文化传播的博客平台，欢迎分享粤剧、粤菜、粤语与民俗相关内容。', 1);

-- =====================================================
-- 14. chat_session（私信会话表）
-- =====================================================
CREATE TABLE chat_session (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user_a BIGINT NOT NULL COMMENT '用户A（较小ID）',
    user_b BIGINT NOT NULL COMMENT '用户B（较大ID）',
    last_msg VARCHAR(100) COMMENT '最后消息预览',
    last_msg_time DATETIME COMMENT '最后消息时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_pair (user_a, user_b),
    FOREIGN KEY (user_a) REFERENCES users(user_id),
    FOREIGN KEY (user_b) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私信会话表';

-- =====================================================
-- 15. chat_message（私信消息表）
-- =====================================================
CREATE TABLE chat_message (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    session_id BIGINT NOT NULL COMMENT '所属会话',
    sender_id BIGINT NOT NULL COMMENT '发送者',
    content VARCHAR(1000) NOT NULL COMMENT '消息内容',
    is_read TINYINT(1) DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    FOREIGN KEY (session_id) REFERENCES chat_session(session_id),
    FOREIGN KEY (sender_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私信消息表';

INSERT INTO chat_session (user_a, user_b, last_msg, last_msg_time) VALUES
(1, 2, '下次一起去听粤剧吗？', '2025-01-20 20:00:00');

INSERT INTO chat_message (session_id, sender_id, content, is_read) VALUES
(1, 2, '陈教授，您好！', 1),
(1, 1, '你好，有什么可以帮到你？', 1),
(1, 2, '下次一起去听粤剧吗？', 0);

-- =====================================================
-- 冗余字段回填
-- =====================================================
UPDATE articles SET excerpt = summary WHERE excerpt IS NULL;

UPDATE articles a
JOIN users u ON a.author_id = u.user_id
JOIN article_category c ON a.category_id = c.category_id
SET a.author_name = u.nickname,
    a.author_avatar = u.avatar,
    a.category = c.category_name;

UPDATE articles a
SET a.tags = (
    SELECT GROUP_CONCAT(t.tag_name ORDER BY t.tag_id SEPARATOR ',')
    FROM article_tag_map m
    JOIN article_tags t ON m.tag_id = t.tag_id
    WHERE m.article_id = a.article_id
);

UPDATE comments c
JOIN users u ON c.user_id = u.user_id
SET c.user_name = u.nickname,
    c.user_avatar = u.avatar;

UPDATE comments c
JOIN comments p ON c.parent_id = p.comment_id
JOIN users u ON p.user_id = u.user_id
SET c.reply_to_user_id = u.user_id,
    c.reply_to_user_name = u.nickname
WHERE c.parent_id IS NOT NULL;

SET FOREIGN_KEY_CHECKS = 1;

SELECT '粤韵志 MySQL 数据库初始化完成' AS message;
"""

if "CREATE TABLE comment_like" not in src:
    src = src.rstrip() + extra

out = header + src
path = pathlib.Path(r"d:\粤韵志\backend\src\main\resources\init-mysql.sql")
path.write_text(out, encoding="utf-8")
print(f"Written {path} ({len(out)} chars)")
