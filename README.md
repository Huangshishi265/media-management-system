# 媒体素材管理系统

一个基于 Spring Boot 3.2 的完整媒体素材管理系统，支持图片、视频、音频、文档等多种类型素材的上传、分类、搜索和管理。

## 📋 项目简介

本项目是一个功能完整的媒体素材管理系统，采用前后端分离架构，后端使用 Spring Boot，前端使用 Thymeleaf 模板引擎和 ECharts 图表库，提供响应式的管理界面。

## ✨ 功能特性

### 系统管理模块
- **用户认证**：支持 JWT Token 认证，密码 BCrypt 加密存储
- **RBAC 权限控制**：基于角色的访问控制，支持用户-角色-菜单三级权限管理
- **用户管理**：用户 CRUD 操作、状态管理、密码重置、角色分配
- **角色管理**：角色 CRUD 操作、权限分配
- **菜单管理**：菜单树形结构管理、权限标识配置

### 素材管理模块
- **素材上传**：支持多类型文件上传（图片、视频、音频、文档），自动类型识别
- **素材管理**：素材 CRUD、上架/下架、推荐设置
- **分类管理**：树形分类结构，支持无限层级
- **标签搜索**：基于标签的素材搜索功能
- **统计分析**：下载量、浏览量统计，热门素材排行

### 数据可视化
- **仪表盘**：ECharts 数据图表展示
- **素材类型分布**：饼图展示各类型素材占比
- **分类素材数量**：柱状图展示分类统计
- **热门素材排行**：TOP 10 热门素材列表

### AI 智能能力（接入智谱 GLM）
- **AI 智能识别**：上传图片一键生成标题/描述/标签/分类建议，自动回填表单（可改）
- **规则兜底分类**：AI 分类名映射到现有分类树，匹配不上归「未分类」由人工选
- **降级不阻塞**：AI 超时 / 未配置 Key 时自动降级为手动填写，入库主流程不受影响
- **Bad Case 回收**：记录「AI 建议 vs 用户最终值」与采纳标记，供产品迭代复盘

### 缓存优化
- **Redis 缓存**：热门素材、分类数据缓存
- **缓存失效**：自动缓存更新与失效策略
- **缓存预热**：系统启动时预热常用数据

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| Redis | 7.x | 缓存数据库 |
| MySQL | 8.0 | 关系数据库 |
| Thymeleaf | 3.x | 模板引擎 |
| ECharts | 5.4.3 | 数据可视化 |
| JWT | 0.12.3 | Token 认证 |
| Druid | 1.2.20 | 数据库连接池 |
| Hutool | 5.8.24 | 工具库 |

## 📁 项目结构

```
media-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/mediams/
│   │   │   ├── config/             # 配置类
│   │   │   ├── controller/         # 控制器
│   │   │   ├── dto/                # 数据传输对象
│   │   │   ├── entity/             # 实体类
│   │   │   ├── exception/          # 异常处理
│   │   │   ├── mapper/             # 数据访问层
│   │   │   ├── security/           # 安全认证
│   │   │   ├── service/            # 业务服务层
│   │   │   └── util/               # 工具类
│   │   └── resources/
│   │       ├── static/             # 静态资源
│   │       ├── templates/          # Thymeleaf 模板
│   │       └── application.yml     # 配置文件
│   └── test/                       # 测试代码
├── sql/
│   └── init.sql                    # 数据库初始化脚本
├── Dockerfile                      # Docker 构建文件
├── docker-compose.yml              # Docker Compose 配置
└── pom.xml                         # Maven 项目配置
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7+

### 本地运行

1. **克隆项目**
```bash
git clone <repository-url>
cd media-management-system
```

2. **初始化数据库**
```bash
# 创建数据库并执行初始化脚本
mysql -u root -p < sql/init.sql
```

3. **修改配置**
编辑 `src/main/resources/application.yml`，修改数据库和 Redis 连接配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/media_db
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

4. **构建并运行**
```bash
# 构建项目
mvn clean package -DskipTests

# 运行项目
java -jar target/media-management-system-1.0.0.jar
```

5. **访问系统**
- 系统地址：http://localhost:8080
- 默认管理员：admin / admin123
- Druid 监控：http://localhost:8080/druid （admin / admin123）

### Docker 部署

1. **一键启动**
```bash
# 启动所有服务（MySQL + Redis + App）
docker-compose up -d
```

2. **查看服务状态**
```bash
docker-compose ps
```

3. **停止服务**
```bash
docker-compose down
```

## 📖 API 文档

### 认证接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/register` | POST | 用户注册 |
| `/api/auth/logout` | POST | 用户退出 |
| `/api/auth/current` | GET | 获取当前用户 |

### 用户管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/system/user/page` | GET | 分页查询用户 |
| `/api/system/user/{id}` | GET | 获取用户详情 |
| `/api/system/user` | POST | 创建用户 |
| `/api/system/user/{id}` | PUT | 更新用户 |
| `/api/system/user/{id}` | DELETE | 删除用户 |

### 素材管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/media/page` | GET | 分页查询素材 |
| `/api/media/{id}` | GET | 获取素材详情 |
| `/api/media/upload` | POST | 上传素材 |
| `/api/media/{id}` | PUT | 更新素材 |
| `/api/media/{id}/publish` | PUT | 上架素材 |
| `/api/media/{id}/unpublish` | PUT | 下架素材 |
| `/api/media/hot` | GET | 热门素材 |
| `/api/media/recommend` | GET | 推荐素材 |
| `/api/media/search/tag` | GET | 标签搜索 |
| `/api/media/ai-analyze` | POST | AI 智能识别（上传图片，返回建议） |
| `/api/media/ai-badcase` | POST | 记录 AI bad case（采纳/修改） |

### 分类管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/media/category/tree` | GET | 分类树 |
| `/api/media/category/{id}` | GET | 分类详情 |
| `/api/media/category` | POST | 创建分类 |
| `/api/media/category/{id}` | PUT | 更新分类 |
| `/api/media/category/{id}` | DELETE | 删除分类 |

## 🔒 安全配置

- **密码加密**：使用 BCryptPasswordEncoder 加密存储
- **JWT Token**：Token 有效期 24 小时
- **权限控制**：基于 Spring Security 的方法级权限控制
- **SQL 注入防护**：MyBatis-Plus 自动参数化查询
- **CSRF 防护**：API 接口禁用 CSRF，页面请求启用

## 📊 数据库表结构

### 系统管理表
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_menu` - 菜单表
- `sys_user_role` - 用户角色关联表
- `sys_role_menu` - 角色菜单关联表

### 素材管理表
- `media` - 素材表
- `media_category` - 素材分类表
- `media_tag` - 标签表
- `ai_bad_case` - AI 打标签 bad case 记录表

## 🤖 AI 产品设计说明

本项目接入了真实的 AI 能力（智谱 GLM 视觉模型），并落地了完整的产品设计闭环——数据清洗、
模型选型、效果评估（采纳率）、成本控制、异常降级、Bad Case 回收、规则 vs 模型边界等。

配置方式：在 `application.yml` 的 `ai.zhipu.api-key` 填入智谱 API Key（https://open.bigmodel.cn 免费申请）。

详细设计文档：[docs/ai-design.md](docs/ai-design.md)

## 🧪 测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest
```

## 📝 开发指南

### 代码规范
- 遵循阿里巴巴 Java 开发规范
- 使用 Lombok 简化代码
- 统一异常处理
- RESTful API 设计

### 分支管理
- `main` - 主分支，稳定版本
- `develop` - 开发分支
- `feature/*` - 功能分支

## 📄 License

MIT License

## 👥 贡献者

感谢所有为本项目做出贡献的开发者！

---

**注意**：本项目仅供学习和参考使用，生产环境使用请根据实际需求进行调整和优化。