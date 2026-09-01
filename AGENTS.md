# AGENTS.md - 项目规范文件

## 项目概览

媒体素材管理系统是一个基于 Spring Boot 3.2 的全栈 Web 应用，提供媒体素材的完整管理功能。

### 核心技术栈
- **框架**: Spring Boot 3.2.0 + Spring Security 6.x
- **ORM**: MyBatis-Plus 3.5.5
- **缓存**: Redis (Spring Data Redis)
- **数据库**: MySQL 8.0 + Druid 连接池
- **前端**: Thymeleaf + ECharts 5.4.3
- **认证**: JWT (jjwt 0.12.3)

### 项目类型
- Java Web 应用（非 Node.js 项目）
- 前后端分离架构（后端 API + Thymeleaf 页面）

## 构建和测试命令

### 构建命令
```bash
# 清理并构建（跳过测试）
mvn clean package -DskipTests

# 完整构建（包含测试）
mvn clean package

# 仅编译
mvn compile
```

### 测试命令
```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 运行指定测试方法
mvn test -Dtest=UserServiceTest#testCreateUser_Success
```

### 运行命令
```bash
# 本地运行
java -jar target/media-management-system-1.0.0.jar

# Maven 运行
mvn spring-boot:run
```

## 代码风格指南

### Java 代码规范
- 遵循阿里巴巴 Java 开发规范
- 使用 Lombok 简化实体类（@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor）
- 类和方法的注释使用 Javadoc 格式
- 变量命名：驼峰命名法，常量使用全大写

### 包结构规范
```
com.mediams
├── config/      # 配置类（@Configuration）
├── controller/  # 控制器（@RestController/@Controller）
├── dto/         # 数据传输对象
├── entity/      # 实体类（@TableName）
├── exception/   # 异常类
├── mapper/      # Mapper 接口（@Mapper）
├── security/    # 安全认证相关
├── service/     # 服务接口
│   └ impl/      # 服务实现（@Service）
└── util/        # 工具类（@Component/@UtilityClass）
```

### API 设计规范
- RESTful API 路径命名
- 统一返回 Result<T> 包装类
- 分页查询使用 PageResult<T>
- POST/PUT 请求使用 @RequestBody
- GET 请求使用 @RequestParam

### 前端代码规范
- Thymeleaf 模板使用 `th:` 前缀
- CSS 类名使用小写和连字符（如：media-card）
- JavaScript 使用 ES6+ 语法
- 使用 async/await 处理异步请求

## 关键文件位置

### 配置文件
- `src/main/resources/application.yml` - 主配置文件
- `pom.xml` - Maven 依赖配置
- `Dockerfile` - Docker 构建配置
- `docker-compose.yml` - Docker Compose 配置

### 安全相关
- `src/main/java/com/mediams/security/SecurityConfig.java` - Security 配置
- `src/main/java/com/mediams/security/JwtAuthenticationFilter.java` - JWT 过滤器
- `src/main/java/com/mediams/util/JwtUtil.java` - JWT 工具类

### 异常处理
- `src/main/java/com/mediams/exception/GlobalExceptionHandler.java` - 全局异常处理器
- `src/main/java/com/mediams/exception/BusinessException.java` - 业务异常

### 数据库
- `sql/init.sql` - 数据库初始化脚本

## 常见问题修复

### 端口冲突
默认端口 8080，可通过 `application.yml` 或环境变量修改：
```yaml
server:
  port: 8080
```

### 数据库连接失败
检查配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/media_db
    username: root
    password: your_db_password
```

### Redis 连接失败
检查 Redis 服务状态：
```bash
redis-cli ping
```

### JWT Token 无效
检查配置的 secret 长度（需 >= 256 bits）：
```yaml
jwt:
  secret: your_jwt_secret_change_me
```

## 安全注意事项

### 密码存储
- 使用 BCryptPasswordEncoder 加密
- 密码强度：至少 10 rounds

### JWT 安全
- Secret 长度 >= 256 bits
- Token 有效期建议 <= 24 小时
- 禁止在 URL 中传递 Token

### 文件上传
- 校验文件类型
- 校验文件大小（默认 100MB）
- 文件存储路径不可预测

### SQL 安全
- 使用 MyBatis-Plus 参数化查询
- 禁止拼接 SQL

## 测试说明

### 单元测试
- 使用 JUnit 5 + Mockito
- 测试类命名：`XxxTest`
- 测试方法命名：`test方法名_场景`

### 测试位置
- `src/test/java/com/mediams/`

### 已覆盖测试
- UserServiceTest - 用户服务测试
- MediaServiceTest - 素材服务测试
- AuthControllerTest - 认证控制器测试
- MediaControllerTest - 素材控制器测试
- GlobalExceptionHandlerTest - 异常处理器测试

## 部署指南

### Docker 部署
```bash
docker-compose up -d
```

### 服务依赖
1. MySQL 8.0 - 数据存储
2. Redis 7.x - 缓存服务
3. Spring Boot App - 应用服务

### 健康检查
- MySQL: `mysqladmin ping -h localhost`
- Redis: `redis-cli ping`
- App: `curl http://localhost:8080/`

## 数据访问指南

### MyBatis-Plus 使用
```java
// 查询示例
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getUsername, username);
User user = userMapper.selectOne(wrapper);

// 分页示例
Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
```

### Redis 缓存使用
```java
// 缓存注解
@Cacheable(value = "hot_media", key = "'hot:' + #limit")
public List<Media> getHotMedia(int limit);

// 缓存失效
@CacheEvict(value = "hot_media", allEntries = true)
public boolean publishMedia(Long id);
```

## 扩展点

### 添加新实体
1. 创建 `entity/Xxx.java`
2. 创建 `mapper/XxxMapper.java`
3. 创建 `service/XxxService.java` 和 `impl/XxxServiceImpl.java`
4. 创建 `controller/XxxController.java`
5. 更新数据库脚本

### 添加新 API
1. 在 Controller 中添加方法
2. 使用 `@GetMapping/@PostMapping` 等注解
3. 返回 `Result<T>` 包装类型
4. 添加 Swagger 文档（可选）

### 添加新页面
1. 创建 `templates/xxx.html`
2. 在 PageController 中添加路由
3. 配置 SecurityConfig 允许访问

## 注意事项

### 重要提醒
1. 本项目运行在 Java 环境，沙箱环境不支持直接运行
2. 需在本地 Java 环境或 Docker 中运行
3. 生产部署前请修改 JWT Secret 和数据库密码
4. 文件上传目录需要配置正确的权限

### 环境差异
- 开发环境：`application.yml` 默认配置
- 生产环境：使用环境变量覆盖配置

### 性能优化
- 使用 Redis 缓存热门数据
- Druid 连接池优化
- 分页查询避免全表扫描