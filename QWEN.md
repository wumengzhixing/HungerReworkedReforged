# Hunger Reworked Reforged 项目上下文文档

## 项目概述

**Hunger Reworked Reforged** 是一个 Minecraft NeoForge 模组（版本 1.21），是对原版饥饿系统的改革重铸喵~

**项目信息**：
- **模组 ID**：`hunger_reworked_reforged`
- **版本**：1.0.2+neoforge_1.21.1
- **许可证**：MIT
- **作者**：wu_meng, Liu Dongyu
- **包名**：`net.mcbbs.uid1525632.hungerreworkedreforged`

**核心功能**：
1. **胃容量系统**：食物不会立即生效，而是存储在胃中逐渐消化喵~
2. **助消化效果**：新增快速消化效果及对应药水系统喵~
3. **铁胃效果**：增加胃容量的药水效果喵~
4. **过饱/呕吐效果**：吃太多会触发过饱效果，严重时会呕吐喵~
5. **Diet 模组兼容**：可选兼容 Diet 营养系统喵~

## 技术栈

| 组件 | 版本/规格 |
|------|----------|
| Java | 21 |
| Minecraft | 1.21 |
| NeoForge | 21.1.219 |
| Gradle | 7.1.20 (neoforge userdev) |
| JUnit | 5.9.2 |
| Mockito | 4.11.0 |
| JaCoCo | 0.8.13 |

## 项目结构

```
HungerReworkedReforged/
├── src/
│   ├── main/
│   │   ├── java/net/mcbbb/uid1525632/hungerreworkedreforged/
│   │   │   ├── HungerReworked.java          # 模组主类喵~
│   │   │   ├── client/                       # 客户端渲染与覆盖层喵~
│   │   │   ├── common/                       # 通用逻辑喵~
│   │   │   │   ├── attachment/               # 玩家胃数据附件喵~
│   │   │   │   ├── config/                   # 配置系统喵~
│   │   │   │   ├── diet/                     # Diet 兼容代理喵~
│   │   │   │   ├── effect/                   # 药水效果实现喵~
│   │   │   │   ├── init/                     # 注册表初始化喵~
│   │   │   │   ├── FoodEventHandler.java     # 食物事件处理器喵~
│   │   │   │   └── CommonSide.java           # 服务端逻辑喵~
│   │   │   ├── mixin/                        # Mixin 注入喵~
│   │   │   └── network/                      # 网络包系统喵~
│   │   └── resources/
│   │       ├── assets/                       # 资源文件喵~
│   │       ├── META-INF/neoforge.mods.toml   # 模组配置喵~
│   │       └── hunger_reworked_reforged.mixins.json
│   └── test/java/                            # 单元测试喵~
├── build.gradle                              # Gradle 构建配置喵~
├── gradle.properties                         # 项目属性配置喵~
└── settings.gradle                           # Gradle 设置喵~
```

## 构建与运行

### 环境要求
- JDK 21 或更高版本喵~
- Gradle 包装器（已包含在项目根目录）喵~

### 构建命令

```bash
# 构建项目喵~
gradlew build

# 运行客户端喵~
gradlew runClient

# 运行服务端喵~
gradlew runServer

# 运行游戏测试喵~
gradlew runGameTest

# 运行单元测试喵~
gradlew test

# 生成测试覆盖率报告喵~
gradlew jacocoTestReport
```

### 配置说明

在 `gradle.properties` 中配置以下参数喵~：

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `minecraft_version` | Minecraft 版本 | 1.21 |
| `neo_version` | NeoForge 版本 | 21.1.219 |
| `mod_version` | 模组版本 | 1.0.2+neoforge_1.21.1 |

## 开发规范

### 代码风格
- **缩进**：使用 Tab 进行缩进喵~
- **大括号**：K&R 风格（起始大括号不换行）喵~
- **命名**：
  - 类名：大驼峰式（如 `PlayerStomach`）喵~
  - 方法/变量名：小驼峰式（如 `getStomachCapability`）喵~
  - 包名：全小写加下划线（如 `net.mcbbs.uid1525632.hungerreworkedreforged`）喵~
- **行长度**：每行不超过 150 字符喵~
- **方法长度**：每个方法不超过 300 行喵~

### 注释规范
- 所有 `public` 类和方法必须有 Javadoc 喵~
- Javadoc 使用中文编写，句尾添加"喵~"语气词喵~
- 复杂逻辑需要行内注释说明喵~

### 测试规范
- 使用 JUnit 5 进行单元测试喵~
- 使用 Mockito 进行 Mock 测试喵~
- 测试类使用 `@ExtendWith(MockitoExtension.class)` 注解喵~
- 测试方法使用 `@DisplayName` 提供中文描述喵~
- 测试断言消息以"喵~"结尾喵~

### 提交规范
- Commit message 使用 `type(scope): subject` 格式喵~
- Type 包括：`feat`, `fix`, `docs`, `chore`, `test`, `style`, `revert`, `ci` 喵~
- Scope 为需求或故事编号（如 `REQ-1`）喵~

## 核心类说明

### PlayerStomach
玩家胃数据附件类，管理食物存储和消化逻辑喵~

**关键字段**：
- `content`：胃中食物列表喵~
- `totalFood`：食物总量喵~
- `foodDigested`：已消化食物量喵~
- `satDigested`：已消化饱和度喵~

**核心方法**：
- `addFood()`：添加食物到胃中喵~
- `digest()`：执行消化过程喵~
- `popFood()`：移除最后一个食物（呕吐）喵~
- `getStomachCapability()`：获取玩家胃容量喵~

### FoodEventHandler
食物事件处理器，拦截玩家进食事件喵~

### 药水效果类
- `FastDigestionEffect`：快速消化效果喵~
- `StrongStomachEffect`：铁胃效果（增加胃容量）喵~
- `OverstuffedEffect`：过饱效果喵~
- `VomitingEffect`：呕吐效果喵~

### 配置类 (Configuration)
可配置参数喵~：
- `FAST_DIGEST_BASE_TICK_RATE`：快速消化基础 Tick 速率（默认 1）喵~
- `FAST_DIGEST_CONSUME_RATE`：快速消化消耗速率（默认 0.1）喵~

## 依赖说明

### 硬依赖
- NeoForge 21.1.219+ 喵~
- Minecraft 1.21-1.21.1 喵~

### 软依赖
- Diet 模组（可选，用于营养系统兼容）喵~

### 测试依赖
- JUnit Jupiter 5.9.2 喵~
- Mockito 4.11.0 喵~

## 注意事项

1. **命名规范**：包名必须全小写，类名大驼峰，不要出现命名不规范的情况喵~
2. **代码质量**：重视代码评审和回归测试，反对快速迭代喵~
3. **异常处理**：优先使用特定异常类型，记录异常时包含上下文信息喵~
4. **资源管理**：优先使用 try-with-resources 管理资源喵~
5. **游戏测试**：使用 JaCoCo 收集游戏测试覆盖率数据喵~

## 相关链接

- [NeoForge 官方文档](https://docs.neoforged.net/)
- [Minecraft 开发 Wiki](https://minecraft.wiki/)
- [Diet 模组 GitHub](https://github.com/TheIllusiveC4/Diet)
