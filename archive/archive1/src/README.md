# 个人任务管理系统

一个基于Java Web技术栈开发的现代化个人任务管理系统，提供直观的用户界面和完整的功能支持。

## 功能特性

### 用户管理
- 用户注册和登录
- 个人信息管理
- 密码修改
- 安全退出

### 任务管理
- 创建、编辑、删除任务
- 任务状态跟踪（待处理、进行中、已完成）
- 任务优先级设置（高、中、低）
- 任务分类管理
- 任务截止日期设置
- 任务搜索和过滤
- 即将到期任务提醒

### 分类管理
- 创建、编辑、删除分类
- 分类描述管理
- 分类关联任务

### 界面特性
- 响应式设计，支持移动端访问
- 现代化UI（基于Element Plus）
- 直观的任务状态展示
- 友好的用户交互体验
- 实时数据更新

## 技术栈

### 后端
- Java 8+
- Servlet 4.0
- MySQL 8.0
- Maven 3.6+

### 前端
- Vue.js 3
- Element Plus
- Axios
- CSS3

## 系统要求

- JDK 8 或更高版本
- MySQL 8.0 或更高版本
- Maven 3.6 或更高版本
- 现代浏览器（Chrome、Firefox、Safari、Edge等）

## 安装说明

1. 克隆项目
```bash
git clone [项目地址]
cd task-management-system
```

2. 配置数据库
- 创建MySQL数据库
- 修改 `src/main/resources/database.properties` 中的数据库配置

3. 编译项目
```bash
mvn clean package
```

4. 部署项目
- 将生成的WAR文件部署到Tomcat等Web服务器
- 或使用Maven插件运行：
```bash
mvn tomcat7:run
```

5. 访问系统
- 打开浏览器访问：`http://localhost:8080/task-management`

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── taskmanagement/
│   │           ├── controller/    # 控制器层
│   │           ├── service/       # 服务层
│   │           ├── dao/          # 数据访问层
│   │           ├── model/        # 数据模型
│   │           ├── filter/       # 过滤器
│   │           └── util/         # 工具类
│   ├── resources/
│   │   ├── database.properties   # 数据库配置
│   │   └── log4j2.xml          # 日志配置
│   └── webapp/
│       ├── css/                 # 样式文件
│       ├── js/                  # JavaScript文件
│       └── index.html          # 主页面
└── test/                       # 测试代码
```

## API文档

### 用户接口
- POST `/api/users` - 用户注册
- POST `/api/users/login` - 用户登录
- POST `/api/users/logout` - 用户退出
- GET `/api/users/current` - 获取当前用户信息
- PUT `/api/users` - 更新用户信息
- PUT `/api/users/password` - 修改密码

### 任务接口
- GET `/api/tasks` - 获取任务列表
- POST `/api/tasks` - 创建任务
- PUT `/api/tasks/{id}` - 更新任务
- DELETE `/api/tasks/{id}` - 删除任务
- GET `/api/tasks/upcoming` - 获取即将到期任务
- GET `/api/tasks/search` - 搜索任务

### 分类接口
- GET `/api/categories` - 获取分类列表
- POST `/api/categories` - 创建分类
- PUT `/api/categories/{id}` - 更新分类
- DELETE `/api/categories/{id}` - 删除分类

## 开发团队

- 开发者：[您的名字]
- 联系方式：[您的邮箱]

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 贡献指南

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

## 更新日志

### v1.0.0 (2024-03-xx)
- 初始版本发布
- 实现基本的任务管理功能
- 实现用户认证和授权
- 实现分类管理功能
- 添加响应式界面设计 