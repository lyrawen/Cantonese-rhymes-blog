-- =====================================================
-- 粤韵志 MySQL 初始化脚本（XAMPP）
-- 用法：在 phpMyAdmin 或 mysql 命令行中执行本文件
-- 默认连接：localhost:3306  用户 root  密码（空）
-- 测试登录密码：123456（所有用户）
-- =====================================================

-- =====================================================
-- 数据库：YueYunZhiDB
-- 描述：粤韵志 · 岭南文化博客数据库
-- 版本：1.0
-- =====================================================

-- 创建数据库
DROP DATABASE IF EXISTS yueyunzhi;
CREATE DATABASE yueyunzhi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yueyunzhi;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. users（用户表）
-- =====================================================
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    bio VARCHAR(255) COMMENT '个人简介',
    role VARCHAR(10) DEFAULT 'user' COMMENT 'user / admin',
    status TINYINT DEFAULT 1 COMMENT '1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    cover_photo VARCHAR(255) COMMENT '个人主页封面'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入用户数据
INSERT INTO users (username, password, email, nickname, avatar, bio, role, status) VALUES
('chen_zhiyuan', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'zhiyuan.chen@example.com', '陈志远', '/avatars/av1.png', '粤剧研究员，专注粤剧历史与当代传承研究二十余年', 'admin', 1),
('liang_xiaowen', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'xiaowen.liang@example.com', '梁晓雯', '/avatars/av2.png', '非遗饮食文化研究者，美食专栏作家', 'user', 1),
('huang_zemin', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'zemin.huang@example.com', '黄泽民', '/avatars/av3.png', '粤语语言学专家，广府文化推广人', 'user', 1),
('zhong_huiyi', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'huiyi.zhong@example.com', '钟惠仪', '/avatars/av4.png', '民俗学者，专注岭南节庆文化研究', 'user', 1),
('wu_jianguo', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'jianguo.wu@example.com', '吴建国', '/avatars/av5.png', '非遗工艺传承人，潮州木雕技艺传承者', 'user', 1),
('su_yongmei', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'yongmei.su@example.com', '苏咏梅', '/avatars/av2.png', '粤乐研究者，南音文化推广人', 'user', 1),
('lin_shaohua', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'shaohua.lin@example.com', '林少华', '/avatars/av3.png', '青年粤剧编剧，传统文化创新者', 'user', 1),
('admin', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'admin@yueyunzhi.com', '系统管理员', '/avatars/admin.png', '平台管理员', 'admin', 1),
('wang_xiufang', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'xiufang.wang@example.com', '王秀芳', '/avatars/av4.png', '广绣非遗传承人，刺绣艺术家', 'user', 1),
('li_weimin', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'weimin.li@example.com', '李为民', '/avatars/av5.png', '岭南建筑学者，古建保护专家', 'user', 1),
('chen_juan', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'juan.chen@example.com', '陈娟', '/avatars/av1.png', '美食作家，粤菜文化研究者', 'user', 1),
('zhang_ming', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'ming.zhang@example.com', '张明', '/avatars/av2.png', '普通用户，岭南文化爱好者', 'user', 1),
('liu_fang', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'fang.liu@example.com', '刘芳', '/avatars/av3.png', '普通用户，热爱粤剧', 'user', 1),
('zhao_wei', '$2a$10$X7VYx8f9gHjKlMnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYzA', 'wei.zhao@example.com', '赵伟', '/avatars/av4.png', '普通用户，关注非遗', 'user', 0); -- 已禁用用户

-- =====================================================
-- 2. article_category（文章分类表）
-- =====================================================
CREATE TABLE article_category (
    category_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 插入分类数据
INSERT INTO article_category (category_name, sort_order) VALUES
('粤剧戏曲', 1),
('粤菜食艺', 2),
('粤语方言', 3),
('民俗节庆', 4),
('非遗工艺', 5),
('历史建筑', 6),
('粤乐南音', 7),
('岭南文学', 8);

-- =====================================================
-- 3. article_tags（标签表）
-- =====================================================
CREATE TABLE article_tags (
    tag_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 插入标签数据
INSERT INTO article_tags (tag_name) VALUES
('戏曲'), ('非遗'), ('精选'), ('文化'), ('历史'), ('传统'), ('美食'), 
('建筑'), ('民俗'), ('南音'), ('木雕'), ('广绣'), ('早茶'), ('龙舟'), 
('醒狮'), ('粤乐'), ('文学'), ('诗词'), ('手工艺'), ('传承');

-- =====================================================
-- 4. articles（文章表）
-- =====================================================
CREATE TABLE articles (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 插入文章数据
INSERT INTO articles (author_id, category_id, title, summary, content, cover_image, status, is_featured, view_count, like_count, comment_count, favorite_count, publish_time) VALUES
(1, 1, '粤剧百年：从省港大班到当代创新的文化演变史', 
 '探寻粤剧艺术在一个世纪风云变幻中的传承脉络，以及新生代艺术家如何将传统程式融入当代美学语境。',
 '广东大戏，俗称粤剧，是岭南文化中最具代表性的艺术形式之一。它诞生于明代，成熟于清末民初，在二十世纪前半叶创造了省港舞台的黄金时代。\n\n## 一、省港大班的传奇岁月（1900—1949）\n\n二十世纪初，随着广州与香港之间航运贸易的繁盛，粤剧班社开始频繁往来于两地演出，形成了独具特色的「省港大班」体制。这一时期涌现出薛觉先、马师曾、白驹荣、廖侠怀等一批划时代的艺术大师，他们广博地吸收西洋音乐、电影表演技巧，大胆改造传统程式，开创了粤剧艺术史上最为蓬勃的革新浪潮。\n\n> 「粤剧者，粤人之剧，粤语之声，粤情之寄托也。」—— 薛觉先\n\n## 二、香港时期的流金岁月（1949—1980）\n\n五十年代起，大批粤剧艺人南下香港，使香港成为粤剧传承的重要基地。任剑辉与白雪仙的黄金组合，以及唐涤生精心编写的《帝女花》《紫钗记》《再世红梅记》等一系列名作，将粤剧推向了一个新的艺术高峰。\n\n### 唐涤生与仙凤鸣的美学革命\n\n唐涤生以其深厚的古典文学修养与精妙的戏剧结构，赋予粤剧脚本全新的文学品格。他与仙凤鸣剧团的合作，创造了粤剧史上最为辉煌的一段黄金岁月。\n\n## 三、新生代的实验探索（2000—今）\n\n进入二十一世纪，粤剧面临的困境愈发严峻——观众老龄化、传承断层、市场萎缩。然而，正是在这种危机感的驱动下，一批年轻的粤剧艺术家开始寻求突破。他们将实验剧场的语汇引入粤剧表演，与爵士音乐、当代舞蹈进行跨界融合，试图以全新的面貌吸引年轻一代走进戏院。\n\n百年粤剧的演变史，折射出岭南文化海纳百川、兼收并蓄的精神底色。在全球化与本土化的张力中，粤剧以其独特的方式书写着每一个时代的故事。',
 '/covers/yueju-100.jpg', 1, 1, 2840, 186, 43, 94, '2025-01-18 10:30:00'),

(2, 2, '消失的味道：广式腊肠手工制法的最后传人',
 '走访番禺沙湾古镇，记录七十八岁老师傅坚守的三十年腊肠手艺，以及传承危机中的困局与希望。',
 '在番禺沙湾古镇的一条深巷里，七十八岁的陈伯每天凌晨四点就开始忙碌。选肉、切丁、调味、灌肠、晾晒……每一道工序都遵循着四十年不变的手工古法。\n\n“现在的年轻人都不愿意学这个了。”陈伯叹了口气，手中的动作却一刻不停。他告诉我们，手工腊肠的关键在于猪肉与肥肉的配比——三肥七瘦，切丁要均匀，不能机器绞碎，否则口感全无。\n\n调味更是秘方，汾酒、酱油、白糖的比例全凭手感。陈伯说，这是他师父传下来的手艺，已经有一百多年历史。晾晒也很讲究，要“日晒夜露”七天七夜，让腊肠充分吸收天地精华。\n\n但如今，愿意学这门手艺的年轻人寥寥无几。陈伯的儿子在城里上班，对腊肠生意不感兴趣。“等我做不动了，这门手艺可能就真的消失了。”\n\n我们走访了周边的腊肠作坊，发现大多数已经改用机器生产。虽然效率提高了，但传统手工腊肠那种独特的口感和风味，正在慢慢淡出人们的记忆。',
 '/covers/lachang.jpg', 1, 0, 980, 74, 18, 51, '2025-01-15 14:20:00'),

(3, 3, '粤语俚语里的珠三角百年生活史',
 '从「劏房」到「打边炉」，一百个粤语词汇背后的城市变迁与岭南人的生活智慧。',
 '粤语不仅是一种语言，更是一部活生生的岭南生活史。每一个俚语背后，都藏着一个时代的故事。\n\n## 「劏房」：城市的生存智慧\n\n“劏”在粤语中是剖开的意思，“劏房”指的是将一个单元房分割成多个小房间出租。这个词的流行，反映了香港、广州等大城市人口密集、居住空间紧张的现状。它既是城市发展的产物，也是普通市民的生存智慧。\n\n## 「打边炉」：冬日的温暖记忆\n\n“打边炉”就是吃火锅，但粤式的打边炉有自己的特色。清水锅底，新鲜食材，讲究的是食材的原汁原味。这个词源于广东人围坐在炉边，一边涮肉一边聊天的场景，体现了岭南人的饮食文化和人情味。\n\n## 「拍拖」：浪漫的水上记忆\n\n“拍拖”指谈恋爱，这个词其实来源于珠江上的花艇。过去，男女约会时常乘坐花艇，船桨拍打水面时，船就会靠近，称为“拍拖”。这个浪漫的起源，让这个词充满诗意。\n\n## 更多俚语\n\n「巴闭」（了不起）、「蛊惑」（狡猾）、「求其」（随便）、「是但」（随意）、「得闲」（有空）……每一个词都是岭南文化的活化石。',
 '/covers/yueyu.jpg', 1, 1, 2130, 156, 28, 73, '2025-01-12 09:15:00'),

(4, 4, '行花街：广州花市的百年记忆与现代变迁',
 '每逢春节前夕，花街绽放岭南年俗的独特光华。但这道传统风景，正在悄悄改变。',
 '“未行花街，不算过年。”在广州，春节前逛花市是必不可少的年俗。从西关的花木小贩到如今遍布全城的迎春花市，行花街的历史已经超过一百年。\n\n## 花市的起源\n\n清同治年间，广州就有了除夕花市。最初只是花农挑担上街售卖，后来逐渐形成规模。民国时期，桨栏路、十八甫一带的花市已经非常热闹。\n\n## 花街的变迁\n\n如今，广州每个区都有自己的迎春花市，从农历腊月二十八持续到大年三十晚。花市上不仅有年花年桔，还有各种工艺品、小吃，成为市民春节前最后的狂欢。\n\n## 花的意义\n\n金桔象征大吉大利，桃花寓意宏图大展，水仙代表吉祥如意……每一种花都有它的寓意。行花街，买的不仅是花，更是对新一年的美好祝愿。\n\n## 记忆与传承\n\n对老广州来说，行花街承载着太多记忆。小时候骑在父亲肩上看花灯，长大后和恋人牵手逛花市，成家后带着孩子买年花……一代代人的故事，在花街上延续。',
 '/covers/huashi.jpg', 1, 0, 3460, 212, 67, 118, '2025-01-10 16:45:00'),

(5, 5, '潮州木雕的刀锋美学：一位匠人的四十年',
 '金漆木雕于盛唐渡海南来，在潮州土壤中生根发芽，成为中国木雕艺术的瑰宝之一。',
 '二十二岁那年，吴建国第一次拿起刻刀。四十年后，他的双手已经布满老茧，但刀法愈发精纯。\n\n潮州木雕以多层镂空、金漆装饰著称，题材多取自戏曲故事、民间传说。一块普通的木头，在匠人手中经过凿、刻、雕、修，最终成为栩栩如生的艺术品。\n\n“最难的是人物开脸。”吴师傅说，“一个人的喜怒哀乐，全在眉眼之间。刻刀稍微偏一点，神韵就全没了。”\n\n他的工作室里，摆放着各种工具——平凿、圆凿、三角凿、斜凿……光是刀具就有上百种。每一件工具都有自己的用途，都需要匠人亲手磨制。\n\n四十年来，吴建国带过十几个徒弟，但坚持下来的只有两三个。“这行太苦了，坐着不动就是一整天，眼睛受不了，腰也受不了。”\n\n但每当看到自己的作品在祠堂、庙宇中熠熠生辉，听到游客的赞叹，他又觉得一切都值得。“老祖宗传下来的手艺，总要有人守着。”',
 '/covers/mudiao.jpg', 1, 1, 1780, 120, 29, 65, '2025-01-08 11:20:00'),

(6, 7, '南音·珠江：消逝的广东说唱艺术寻踪',
 '南音是粤地最古老的说唱形式，曾经回荡于珠江两岸的茶楼与木船。如今，它的余音在何处？',
 '夜幕降临，珠江上曾经飘荡着悠扬的南音。这种古老的说唱艺术，以粤语演唱，配以椰胡、筝、箫等乐器，曾是广东地区最流行的娱乐形式。\n\n南音的历史可以追溯到明代，兴盛于清中叶。当时，广州的茶楼酒肆多有南音演唱，珠江上的花艇也是南音的重要演出场所。艺人怀抱椰胡，自拉自唱，诉说着才子佳人的故事，吟唱着世态炎凉。\n\n著名南音曲目有《客途秋恨》《玉葵宝扇》《禅院钟声》等。其中《客途秋恨》讲述文人骚客的离愁别绪，唱腔婉转，词藻优美，是南音艺术的巅峰之作。\n\n但随着时代变迁，南音逐渐衰落。现在的年轻人很少听过南音，甚至不知道这种艺术形式的存在。老一辈艺人相继离世，传承面临断层。\n\n幸运的是，近年来一些文化工作者开始关注南音。他们收集整理曲谱，录制老艺人的演唱，尝试让这门古老的艺术重焕生机。',
 '/covers/nanyin.jpg', 1, 0, 890, 67, 15, 31, '2025-01-05 13:30:00'),

(1, 1, '《帝女花》的前世今生：从南音叙事到粤剧经典',
 '追溯这部粤剧名作七十年的演变历程，探寻唐涤生如何将历史悲剧升华为永恒的艺术经典。',
 '《帝女花》是粤剧史上最负盛名的作品之一。它讲述明末长平公主与驸马周世显在国破家亡之际的悲欢离合，以“香夭”一幕最为动人。\n\n## 从南音到粤剧\n\n《帝女花》的故事最早见于清代戏曲《帝女花传奇》。20世纪50年代，编剧唐涤生将其改编为粤剧，由任剑辉、白雪仙主演，从此成为仙凤鸣剧团的镇团之宝。\n\n## 唐涤生的改编\n\n唐涤生保留了原作的悲剧内核，但在唱词上进行了大胆创新。他将南音、梆子、二黄等曲调熔于一炉，创造出独具特色的音乐风格。“香夭”一曲更是将粤剧唱腔艺术推向极致。\n\n## 永恒的经典\n\n“落花满天蔽月光，借一杯附荐凤台上……”每当这段唱腔响起，观众无不为之动容。《帝女花》不仅在香港、广东广为流传，在海外华人社区也享有盛誉，成为粤剧艺术的代表作。',
 '/covers/dinvhua.jpg', 1, 1, 1240, 88, 21, 42, '2025-01-03 15:00:00'),

(2, 2, '广东早茶文化的社交哲学与历史溯源',
 '一盅两件，茶楼风云。早茶不仅是饮食，更是岭南人的社交方式和生活方式。',
 '“得闲饮茶”是广东人最常用的客套话。早茶文化源远流长，可以追溯到清咸丰年间。当时广州出现“一厘馆”，供应茶水点心，供路人歇脚。后来发展成茶楼，成为市民社交的重要场所。\n\n早茶的“一盅两件”——一壶茶，两件点心，是最经典的搭配。虾饺、烧卖、叉烧包、肠粉……每样点心都有讲究。虾饺要皮薄馅靓，烧卖要肉鲜多汁，叉烧包要“开口笑”。\n\n但早茶的核心不仅是美食，更是社交。老友相聚，谈天说地；家人团圆，其乐融融；商务洽谈，增进感情。在茶楼里，人们可以放松心情，享受慢生活。',
 '/covers/zaocha.jpg', 1, 1, 5620, 312, 98, 156, '2025-01-01 08:30:00'),

(9, 5, '广绣针法图谱：岭南刺绣非遗技艺全解析',
 '广绣是广东四大名绣之首，以题材广泛、造型生动、色彩浓艳而著称。',
 '广绣以广州为中心，流传于珠三角地区，已有千年历史。它以构图饱满、色彩富丽、针法多变著称，与苏绣、湘绣、蜀绣并称中国四大名绣。\n\n广绣的针法多达百余种，常用的有直针、扭针、续针、铺针等。绣制人物、花鸟、山水各有专长。尤其是绣制荔枝、红棉等岭南风物，更是独树一帜。\n\n王秀芳从事广绣四十年，是这项非遗的代表性传承人。她说：“广绣最难的是开脸，人物表情要传神，眼睛要有神采。这需要几十年的功力。”',
 '/covers/guangxiu.jpg', 1, 0, 760, 54, 12, 23, '2025-01-16 10:00:00'),

(10, 6, '西关大屋：晚清广州的居住美学',
 '镬耳墙、青砖、满洲窗……西关大屋里藏着一个家族的世纪记忆。',
 '西关大屋是广州传统民居的代表，兴盛于清末民初。它融合了岭南建筑特色和西洋建筑元素，体现了广州作为通商口岸的文化交融。\n\n西关大屋的特点是“三间两廊”，青砖石脚，镬耳封火墙。内部装饰精美，木雕、砖雕、灰塑、壁画应有尽有。满洲窗是西关大屋的标志，彩色玻璃拼花，阳光下斑斓夺目。\n\n如今，幸存的西关大屋已经不多了。荔湾区的几处被列为文物保护单位，向公众开放。走进西关大屋，仿佛穿越回百年前的广州，感受那个时代的生活气息。',
 '/covers/xiguan.jpg', 1, 0, 1560, 98, 34, 47, '2024-12-28 14:15:00');

-- =====================================================
-- 5. article_tag_map（文章标签关联表）
-- =====================================================
CREATE TABLE article_tag_map (
    article_id BIGINT NOT NULL COMMENT '文章',
    tag_id INT NOT NULL COMMENT '标签',
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES articles(article_id),
    FOREIGN KEY (tag_id) REFERENCES article_tags(tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 插入文章标签关联数据
INSERT INTO article_tag_map (article_id, tag_id) VALUES
(1, 1), (1, 3), (1, 4), (1, 5),  -- 粤剧百年：戏曲、精选、文化、历史
(2, 2), (2, 6), (2, 13), (2, 20), -- 广式腊肠：非遗、传统、美食、传承
(3, 4), (3, 5), (3, 6),          -- 粤语俚语：文化、历史、传统
(4, 9), (4, 14), (4, 4),          -- 行花街：民俗、龙舟、文化
(5, 2), (5, 11), (5, 20), (5, 3), -- 潮州木雕：非遗、木雕、传承、精选
(6, 16), (6, 4), (6, 20),         -- 南音：粤乐、文化、传承
(7, 1), (7, 3), (7, 5),           -- 帝女花：戏曲、精选、历史
(8, 13), (8, 4), (8, 6), (8, 3),  -- 早茶文化：美食、文化、传统、精选
(9, 2), (9, 12), (9, 20),         -- 广绣：非遗、广绣、传承
(10, 8), (10, 5), (10, 4);        -- 西关大屋：建筑、历史、文化

-- =====================================================
-- 6. comments（评论表）
-- =====================================================
CREATE TABLE comments (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 插入评论数据
INSERT INTO comments (article_id, user_id, parent_id, content, status) VALUES
(1, 2, NULL, '读完深有感触！唐涤生与仙凤鸣的故事真的是粤剧史上最动人的一章。特别是《帝女花》的「香夭」一幕，每次听到都会落泪。文章把这段历史梳理得非常清晰，感谢作者的用心研究。', 1),
(1, 1, 1, '谢谢！《帝女花》确实是粤剧中最具感染力的作品之一。有机会可以专门写一篇关于唐涤生编剧艺术的深度文章。', 1),
(1, 4, NULL, '省港大班那段历史我特别感兴趣，薛觉先对西洋音乐的引入真的是革命性的。请问作者有没有关于这一时期录音资料的推荐？想深入了解当时的唱腔风格。', 1),
(2, 3, NULL, '我外公以前也是做腊肠的，看到这篇文章特别有感触。传统手艺真的需要更多人关注和传承。', 1),
(2, 5, 4, '同感！我是做非遗研究的，如果有机会可以一起去拜访这位老师傅。', 1),
(3, 6, NULL, '「拍拖」的来源原来这么浪漫，学到了！', 1),
(4, 7, NULL, '每年过年都要去行花街，这是广州人过年的仪式感。', 1),
(4, 2, 6, '是的，我家三代人都住在广州，行花街的记忆贯穿了我的童年到现在。', 1),
(5, 8, NULL, '吴师傅是我很敬佩的手艺人，他的作品在开元寺也能看到。', 1),
(6, 9, NULL, '南音真的太美了，希望能有更多人了解和传承。', 1),
(1, 10, NULL, '作为粤剧爱好者，这篇文章让我对粤剧历史有了更深的了解。', 1),
(2, 11, NULL, '广式腊肠是我最喜欢的腊味，希望能一直吃到手工制作的。', 1),
(3, 12, NULL, '粤语真的很有意思，每一个俚语背后都有故事。', 1),
(4, 13, NULL, '今年花市一定要去！', 1),
(5, 14, NULL, '潮州木雕的细节真的太精美了。', 1);

-- =====================================================
-- 7. article_like（点赞表）
-- =====================================================
CREATE TABLE article_like (
    user_id BIGINT NOT NULL COMMENT '用户',
    article_id BIGINT NOT NULL COMMENT '文章',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (user_id, article_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (article_id) REFERENCES articles(article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 插入点赞数据
INSERT INTO article_like (user_id, article_id) VALUES
(1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(2, 1), (2, 3), (2, 4), (2, 7), (2, 8),
(3, 1), (3, 2), (3, 4), (3, 5), (3, 8),
(4, 1), (4, 2), (4, 3), (4, 5), (4, 6), (4, 7), (4, 8),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5),
(6, 1), (6, 3), (6, 6), (6, 7),
(7, 1), (7, 2), (7, 4), (7, 8),
(8, 1), (8, 2), (8, 5), (8, 9), (8, 10),
(9, 2), (9, 5), (9, 6), (9, 9),
(10, 1), (10, 4), (10, 7), (10, 10);

-- =====================================================
-- 8. article_favorite（收藏表）
-- =====================================================
CREATE TABLE article_favorite (
    user_id BIGINT NOT NULL COMMENT '用户',
    article_id BIGINT NOT NULL COMMENT '文章',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (user_id, article_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (article_id) REFERENCES articles(article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 插入收藏数据
INSERT INTO article_favorite (user_id, article_id) VALUES
(1, 3), (1, 5), (1, 8),
(2, 1), (2, 4), (2, 7), (2, 8),
(3, 1), (3, 2), (3, 6),
(4, 1), (4, 3), (4, 5), (4, 8), (4, 10),
(5, 2), (5, 4), (5, 5),
(6, 1), (6, 3), (6, 6),
(7, 2), (7, 4), (7, 8),
(8, 1), (8, 5), (8, 9),
(9, 2), (9, 5), (9, 6),
(10, 1), (10, 4), (10, 7), (10, 10),
(11, 8), (11, 3),
(12, 1), (12, 4),
(13, 2), (13, 5);

-- =====================================================
-- 9. article_view（浏览记录表）
-- =====================================================
CREATE TABLE article_view (
    user_id BIGINT NOT NULL COMMENT '用户',
    article_id BIGINT NOT NULL COMMENT '文章',
    view_times INT DEFAULT 1 COMMENT '浏览次数',
    last_view_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最近浏览',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '首次浏览',
    PRIMARY KEY (user_id, article_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (article_id) REFERENCES articles(article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览记录表';

-- 插入浏览记录数据
INSERT INTO article_view (user_id, article_id, view_times, last_view_time) VALUES
(1, 1, 5, '2025-01-20 10:30:00'),
(1, 2, 3, '2025-01-19 15:20:00'),
(1, 3, 2, '2025-01-18 09:15:00'),
(2, 1, 4, '2025-01-20 11:45:00'),
(2, 3, 3, '2025-01-19 16:30:00'),
(2, 4, 2, '2025-01-18 14:20:00'),
(3, 1, 6, '2025-01-20 09:30:00'),
(3, 2, 4, '2025-01-19 13:15:00'),
(3, 5, 3, '2025-01-18 17:40:00'),
(4, 1, 8, '2025-01-20 08:30:00'),
(4, 2, 5, '2025-01-19 10:20:00'),
(4, 3, 4, '2025-01-18 11:10:00'),
(4, 4, 3, '2025-01-17 15:45:00'),
(5, 1, 4, '2025-01-19 14:30:00'),
(5, 2, 3, '2025-01-18 16:20:00'),
(5, 5, 2, '2025-01-17 09:15:00'),
(6, 1, 2, '2025-01-17 09:15:00'),
(6, 3, 1, '2025-01-16 10:00:00'),
(7, 1, 3, '2025-01-19 18:00:00'),
(8, 5, 2, '2025-01-18 12:00:00'),
(9, 2, 1, '2025-01-17 14:00:00'),
(10, 8, 4, '2025-01-20 09:00:00');

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
