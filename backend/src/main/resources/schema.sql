CREATE DATABASE YueyunzhiDB;
GO

USE YueyunzhiDB;
GO

CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(100) NOT NULL,
    username NVARCHAR(50),
    phone NVARCHAR(20),
    bio NVARCHAR(500),
    avatar NVARCHAR(200),
    is_verified BIT NOT NULL DEFAULT 0,
    role INT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2
);
GO

CREATE TABLE articles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    excerpt NVARCHAR(500),
    cover_image NVARCHAR(500),
    author_id BIGINT NOT NULL,
    author_name NVARCHAR(50),
    author_avatar NVARCHAR(200),
    category NVARCHAR(50),
    tags NVARCHAR(255),
    views INT NOT NULL DEFAULT 0,
    likes INT NOT NULL DEFAULT 0,
    comments INT NOT NULL DEFAULT 0,
    is_featured INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    FOREIGN KEY (author_id) REFERENCES users(id)
);
GO

CREATE TABLE comments (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    article_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_name NVARCHAR(100),
    user_avatar NVARCHAR(200),
    content NVARCHAR(MAX) NOT NULL,
    parent_id BIGINT,
    likes INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,
    FOREIGN KEY (article_id) REFERENCES articles(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id)
);
GO

CREATE INDEX idx_articles_category ON articles(category);
CREATE INDEX idx_articles_author ON articles(author_id);
CREATE INDEX idx_articles_status ON articles(status);
CREATE INDEX idx_articles_featured ON articles(is_featured);
CREATE INDEX idx_comments_article ON comments(article_id);
CREATE INDEX idx_comments_parent ON comments(parent_id);
GO

INSERT INTO users (email, password, username, bio, avatar, is_verified, role) VALUES
('admin@yueyunzhi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', '粤韵志管理员账号', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 1, 1),
('chenwenxuan@yueyunzhi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陈文轩', '粤剧文化研究者，岭南文化学者。致力于粤剧文化的传承与保护，著有《粤剧艺术概论》等多部作品。', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chen', 1, 0),
('linyaqin@yueyunzhi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '林雅琴', '岭南文化学者，专注于民俗文化和传统艺术研究。', 'https://api.dicebear.com/7.x/avataaars/svg?seed=lin', 1, 0),
('zhangzhiming@yueyunzhi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张志明', '美食文化评论家，粤菜文化推广者。', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhang', 1, 0),
('wangmeiling@yueyunzhi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王美玲', '粤语文化研究者，方言保护专家。', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wang', 1, 0),
('lijianhua@yueyunzhi.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李建华', '民俗文化学者，传统节庆文化研究者。', 'https://api.dicebear.com/7.x/avataaars/svg?seed=li', 1, 0);
GO

INSERT INTO articles (title, content, excerpt, cover_image, author_id, author_name, author_avatar, category, tags, views, likes, comments, is_featured, status) VALUES
('粤剧：岭南文化的活化石', '粤剧作为岭南地区最具代表性的戏曲艺术形式，承载着深厚的文化底蕴和历史传承。本文将带您深入了解粤剧的起源、发展历程以及独特的艺术魅力。

粤剧的起源与发展

粤剧，又称广东大戏，是中国南方最具代表性的戏曲剧种之一，流行于广东、广西、香港、澳门等粤语地区。粤剧的历史可以追溯到明朝中叶，当时广东地区已经出现了各种地方戏曲形式。

到了清朝雍正年间，随着外江戏（即外省戏曲）的传入，广东本地戏曲开始吸收外来戏曲的精华，逐渐形成了独具特色的粤剧。经过数百年的发展，粤剧已经成为岭南文化的重要组成部分，被誉为"南国红豆"。

粤剧的艺术特色

粤剧以其独特的艺术魅力而闻名于世。首先，粤剧的唱腔优美动听，融合了广东民间音乐的元素，既有高亢激昂的唱段，也有婉转悠扬的旋律。粤剧的唱腔分为大喉、平喉、子喉三种，各有特色。

其次，粤剧的表演形式丰富多彩，包括唱、做、念、打等多种表演技巧。演员通过精湛的表演技巧，将人物的性格和情感生动地展现出来。粤剧的服装和道具也非常精美，充分体现了岭南文化的审美特色。

粤剧的传承与保护

随着时代的发展，粤剧面临着传承和保护的挑战。为了保护和传承这一珍贵的文化遗产，各级政府和文化机构采取了一系列措施。例如，将粤剧列入国家级非物质文化遗产名录，在学校开设粤剧课程，培养年轻一代的粤剧爱好者。

同时，粤剧也在不断创新，融入现代元素，吸引更多的年轻观众。许多粤剧艺术家通过改编经典剧目、创作新剧目等方式，让粤剧焕发新的生机和活力。

结语

粤剧作为岭南文化的重要组成部分，承载着深厚的历史文化底蕴。保护和传承粤剧，不仅是对传统文化的尊重，更是对文化多样性的维护。让我们共同努力，让粤剧这一岭南文化的瑰宝永远绽放光彩。', '粤剧作为岭南地区最具代表性的戏曲艺术形式，承载着深厚的文化底蕴和历史传承。本文将带您深入了解粤剧的起源、发展历程以及独特的艺术魅力。', 'https://images.unsplash.com/photo-1518998053901-5348d3961a04?w=800', 2, '陈文轩', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chen', '粤剧', '粤剧,精选', 2300, 186, 42, 1, 1),
('广式早茶的文化内涵', '早茶文化是广东人生活方式的缩影，体现了"一盅两件"的生活哲学。从虾饺、烧卖到叉烧包，每一道点心都承载着深厚的文化内涵。

早茶的起源

广式早茶文化起源于清朝同治年间，最初是作为商人交流的场所。随着时间的推移，早茶逐渐演变成广东人日常生活的重要组成部分。在广东，"饮茶"不仅仅是喝茶，更是一种社交方式和生活态度。

早茶的文化内涵

早茶文化体现了广东人"慢生活"的生活哲学。在快节奏的现代生活中，早茶为人们提供了一个放松身心、与家人朋友交流的空间。这种文化传统体现了广东人对生活品质的追求和对人际关系的重视。

早茶的点心文化

广式早茶的点心种类繁多，每一种都有其独特的制作工艺和文化内涵。虾饺的晶莹剔透、烧卖的鲜嫩多汁、叉烧包的香甜可口，都是广式点心的代表。这些点心不仅美味，更承载着广东人的饮食智慧和文化传承。', '早茶文化是广东人生活方式的缩影，体现了"一盅两件"的生活哲学。从虾饺、烧卖到叉烧包，每一道点心都承载着深厚的文化内涵。', 'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=800', 3, '张志明', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhang', '粤菜', '粤菜,美食', 2100, 198, 56, 1, 1),
('粤语方言的传承与保护', '粤语作为岭南文化的重要载体，承载着丰富的历史文化信息。在现代化进程中，如何保护和传承这一珍贵的语言文化遗产，是我们需要思考的重要问题。

粤语的历史渊源

粤语，又称广东话、广府话，是汉语七大方言之一，主要流行于广东、广西、香港、澳门等地区。粤语的历史可以追溯到秦汉时期，经过长期的发展演变，形成了独特的语音、词汇和语法体系。

粤语的文化价值

粤语不仅是交流工具，更是岭南文化的重要载体。粤语的语音、词汇中蕴含着丰富的历史文化信息，反映了广东地区的历史变迁和文化传承。许多粤语俗语、歇后语都承载着深厚的文化内涵。

粤语的传承挑战

随着普通话的推广和城市化进程的加快，粤语面临着传承的挑战。许多年轻人对粤语的掌握程度不如前辈，粤语的使用场景也在逐渐减少。如何保护和传承粤语文化，成为了一个重要的社会议题。

粤语的传承路径

为了保护和传承粤语文化，社会各界采取了多种措施。在教育领域，一些学校开设了粤语课程；在媒体领域，粤语电视节目、电影、音乐等作品不断涌现；在社区层面，粤语文化活动丰富多彩。这些努力为粤语的传承和发展提供了有力支持。', '粤语作为岭南文化的重要载体，承载着丰富的历史文化信息。在现代化进程中，如何保护和传承这一珍贵的语言文化遗产，是我们需要思考的重要问题。', 'https://images.unsplash.com/photo-1506477331477-33d5d8b3dc85?w=800', 4, '王美玲', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wang', '粤语', '粤语,文化', 1500, 126, 38, 0, 1),
('岭南传统节庆文化', '从春节到端午，从中秋到重阳，岭南地区的传统节庆蕴含着深厚的文化内涵。这些节庆不仅是时间的标记，更是文化传承的重要载体。

春节文化

春节是岭南地区最重要的传统节日之一。广东人的春节习俗丰富多彩，包括贴春联、放鞭炮、舞狮舞龙、吃年夜饭等。其中，舞狮是岭南春节最具特色的活动之一，象征着驱邪避凶、带来好运。

端午节

端午节在岭南地区有着独特的庆祝方式。除了吃粽子、赛龙舟外，广东人还有"扒龙船"、"洗龙船水"等习俗。这些习俗体现了岭南人民对传统文化的传承和创新。

中秋节

中秋节是岭南地区另一个重要的传统节日。广东人的中秋节习俗包括赏月、吃月饼、猜灯谜等。在一些地区，还有"烧塔"、"舞火龙"等独特的庆祝活动。

重阳节

重阳节在岭南地区也有其独特的庆祝方式。广东人有登高、赏菊、吃重阳糕等习俗。在一些地区，还有"放风筝"、"插茱萸"等传统活动。', '从春节到端午，从中秋到重阳，岭南地区的传统节庆蕴含着深厚的文化内涵。这些节庆不仅是时间的标记，更是文化传承的重要载体。', 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800', 5, '李建华', 'https://api.dicebear.com/7.x/avataaars/svg?seed=li', '民俗', '民俗,节庆', 1900, 167, 45, 0, 1),
('广绣技艺的传承之路', '广绣作为中国传统四大名绣之一，以其精湛的技艺和独特的艺术风格闻名于世。本文将带您了解广绣的历史渊源、技艺特点和传承现状。

广绣的历史

广绣，又称粤绣，是中国四大名绣之一，起源于唐朝，兴盛于明清。广绣以其色彩鲜艳、构图饱满、形象生动而著称，是岭南文化的重要组成部分。

广绣的技艺特点

广绣的技艺特点主要体现在以下几个方面：一是色彩运用大胆，善于运用对比色；二是构图饱满，层次分明；三是针法多样，有数十种不同的针法；四是题材广泛，既有传统图案，也有现代题材。

广绣的传承现状

随着时代的发展，广绣面临着传承的挑战。为了保护和传承这一珍贵的非物质文化遗产，各级政府和文化机构采取了一系列措施。例如，建立广绣传承基地，培养年轻一代的广绣艺人；开展广绣进校园活动，让更多年轻人了解和喜爱广绣。', '广绣作为中国传统四大名绣之一，以其精湛的技艺和独特的艺术风格闻名于世。本文将带您了解广绣的历史渊源、技艺特点和传承现状。', 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 2, '陈文轩', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chen', '非遗', '非遗,广绣', 1300, 98, 28, 0, 1),
('岭南建筑的独特魅力', '岭南建筑融合了中原建筑文化与地方特色，形成了独具一格的建筑风格。从骑楼到祠堂，从园林到民居，岭南建筑展现了独特的艺术魅力。

岭南建筑的特点

岭南建筑的特点主要体现在以下几个方面：一是注重通风采光，适应岭南地区的气候特点；二是装饰精美，雕刻、彩绘等工艺精湛；三是布局灵活，因地制宜；四是材料多样，既有传统的砖木结构，也有现代的钢筋混凝土。

骑楼建筑

骑楼是岭南地区最具特色的建筑形式之一。骑楼建筑的特点是底层架空，形成连续的骑楼走廊，既方便行人遮阳避雨，又增加了商业空间。骑楼建筑在广东、广西、香港等地广泛分布，是岭南城市的重要景观。

岭南园林

岭南园林是中国园林艺术的重要组成部分，以其精巧的布局、精美的装饰和独特的风格而著称。岭南园林注重与自然环境的融合，体现了"天人合一"的哲学思想。', '岭南建筑融合了中原建筑文化与地方特色，形成了独具一格的建筑风格。从骑楼到祠堂，从园林到民居，岭南建筑展现了独特的艺术魅力。', 'https://images.unsplash.com/photo-1548625361-987820eb5b78?w=800', 2, '陈文轩', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chen', '建筑', '建筑,岭南', 1600, 134, 36, 0, 1);
GO

INSERT INTO comments (article_id, user_id, user_name, user_avatar, content, parent_id, likes, status) VALUES
(1, 3, '林雅琴', 'https://api.dicebear.com/7.x/avataaars/svg?seed=lin', '粤剧确实是我们岭南文化的瑰宝，这篇文章写得很详细，让我对粤剧有了更深入的了解。希望能有更多人关注和传承粤剧文化。', NULL, 23, 1),
(1, 2, '陈文轩', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chen', '感谢您的支持！粤剧文化确实需要我们共同努力去传承和保护。', 1, 12, 1),
(1, 3, '张志明', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhang', '作为广东人，我为粤剧感到自豪！这篇文章让我想起了小时候跟着爷爷去看粤剧的时光，那些美好的记忆历历在目。', NULL, 18, 1),
(1, 4, '王美玲', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wang', '写得真好！粤剧的唱腔和表演都很有特色，希望能在学校里推广粤剧教育，让更多年轻人了解和喜欢粤剧。', NULL, 15, 1),
(2, 4, '王美玲', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wang', '早茶文化真是太棒了！每次回家都要去茶楼喝早茶，感觉特别亲切。', NULL, 32, 1),
(2, 5, '李建华', 'https://api.dicebear.com/7.x/avataaars/svg?seed=li', '文章写得很详细，让我对早茶文化有了更深的了解。原来早茶不仅仅是喝茶，更是一种生活态度。', NULL, 28, 1);
GO