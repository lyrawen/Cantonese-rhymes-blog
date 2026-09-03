# 粤韵志 · Cantonese Rhymes Blog

> 记录粤文化的博客系统 —— 文章分享、粤语 AI 助手、粤语翻译与语音合成、社区互动与内容管理。
> A blog dedicated to Cantonese culture — articles, a Cantonese AI assistant, translation & text-to-speech, community interactions and an admin dashboard.

## 功能特色 / Features

- 📰 **粤文化内容社区**：文章发布与浏览、点赞、收藏、评论、关注作者、分类与标签
- 🏆 **榜单与探索**：热门排行、内容探索
- 🤖 **粤文化 AI 助手**：基于 DeepSeek，用粤语解答粤剧、饮食、习俗等文化话题
- 🗣 **粤语翻译与语音**：粤普互译（百度翻译）、粤语男声语音合成（百度语音）
- 💬 **实时聊天**：WebSocket 私信
- 👤 **用户体系**：JWT 登录、个人主页、私信
- 🛠 **管理后台**：用户、文章、分类、标签、评论、公告、运营统计

## 技术栈 / Tech Stack

| 层 | 技术 |
|---|---|
| 前端 Frontend | Vue 3 · Vite 5 · Pinia · Vue Router 4 · ECharts · GSAP · Marked |
| 后端 Backend | Spring Boot 3.2 (Java 17) · Spring Data JPA · Spring Security · WebSocket · Redis |
| 数据库 Database | MySQL |
| 第三方 Third-party | DeepSeek（AI 对话）· 百度翻译 · 百度语音合成 |

## 目录结构 / Structure

```
├── src/                 # 前端源码（Vue 3）
│   ├── views/           #   页面：首页 / 探索 / 详情 / 榜单 / 写作 / 管理后台 ...
│   ├── components/      #   通用与管理端组件
│   ├── stores/          #   Pinia 状态
│   ├── router/          #   路由
│   └── api/             #   接口封装
├── backend/             # 后端源码（Spring Boot）
│   ├── src/main/java/com/yueyunzhi/
│   │   ├── controller/  #   文章 / 用户 / 评论 / AI / 语音 / 翻译 / 上传 ...
│   │   ├── service/     #   业务逻辑
│   │   ├── repository/  #   数据访问（JPA）
│   │   ├── entity/      #   实体
│   │   └── config/      #   Security / Redis / WebSocket / CORS 配置
│   └── src/main/resources/
│       ├── application.properties    # 常规配置（密钥为占位符）
│       └── avatars/                  # 默认头像
├── init-mysql.sql       # 建库 + 初始数据脚本
├── vite.config.js       # 前端开发代理（4000 → 8081）
└── package.json
```

## 本地运行 / Getting Started

前置依赖：Node.js、JDK 17、Maven、MySQL、Redis。

### 1. 数据库 / Database

执行根目录 `init-mysql.sql` 建库建表并导入初始数据（库名 `yueyunzhi`）。

### 2. 后端 / Backend

```bash
cd backend
mvn spring-boot:run        # 默认端口 8081
```

启动前在 `backend/src/main/resources/application.properties` 按需修改数据库与 Redis 连接。

> 🔑 **第三方密钥不入库**：DeepSeek / 百度语音 / 百度翻译的真实 Key 不要写在 `application.properties`，请创建本地文件
> `backend/src/main/resources/application-secret.properties`（已在 `.gitignore` 中，不会提交），填入同名键覆盖占位符即可，例如：
> ```properties
> deepseek.api.key=sk-xxxx
> baidu.speech.api-key=xxxx
> baidu.speech.secret-key=xxxx
> baidu.translate.api-key=xxxx
> ```

### 3. 前端 / Frontend

```bash
npm install
npm run dev                # http://localhost:4000
```

`vite.config.js` 已配置代理：`/api`、`/uploads`、`/avatars`、`/ws` 会转发到 `http://localhost:8081`，前后端需同时启动。

## 默认端口 / Ports

| 服务 | 端口 |
|---|---|
| 前端 Vite | 4000 |
| 后端 Spring Boot | 8081 |
| MySQL | 3306 |
| Redis | 6379 |

## 说明 / Notes

- 后端 `backend/src/main/resources/schema.sql`、`init-mysql.sql` 为结构与初始化脚本，供参考；完整初始数据见根目录 `init-mysql.sql`。
- 管理后台路径 `/admin`，需要管理员账号登录。
