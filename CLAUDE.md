# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在此代码仓库中工作的指导喵~

## Project Overview

Hunger Reworked Reforged 是一个 Minecraft NeoForge 1.21.1 模组，修改了游戏中食物的工作方式喵~

该模组实现了一个胃容量系统，食物不会立即生效，而是先存储在玩家的胃中，然后逐渐消化喵~

## Build and Development Commands

### Build Commands
```bash
# 构建项目喵~
./gradlew build

# 数据生成（必须在构建前执行）喵~
./gradlew runData

# 清理构建产物喵~
./gradlew clean
```

### Run Commands
```bash
# 启动客户端（用于测试模组）喵~
./gradlew runClient

# 启动服务器喵~
./gradlew runServer

# 启动游戏测试服务器喵~
./gradlew runGameTestServer
```

### Test Commands
```bash
# 运行单元测试喵~
./gradlew test

# 运行单元测试并生成 JaCoCo 覆盖率报告喵~
./gradlew test jacocoTestReport

# 查看覆盖率报告喵~
# HTML 报告位于: build/reports/jacoco/test/html/index.html
# XML 报告位于: build/reports/jacoco/test/jacocoTestReport.xml
```

### Other Commands
```bash
# 生成 IntelliJ IDEA 配置喵~
./gradlew idea

# 生成 Eclipse 配置喵~
./gradlew eclipse
```

## Project Architecture

### Core System: Stomach and Digestion

该模组的核心是 `PlayerStomach` 系统，位于 `common/attachment/` 包中：

- **PlayerStomach**: 玩家胃数据附件，存储和管理玩家胃中的食物及其消化状态喵~
- **Food**: PlayerStomach 的内部类，表示胃中的单个食物条目喵~
- **FoodEventHandler**: 处理食物系统相关事件，包括玩家 Tick 时的消化逻辑、玩家克隆时的数据保留、玩家登录时的数据同步喵~

### Diet Compatibility Layer

`common/diet/` 包提供了与 Diet 模组的兼容层：

- **ProxyDiet/PresentDiet**: 使用代理模式实现可选的 Diet 模组集成喵~
- **ProxyIDietTracker/PresentIDietTracker**: Diet 追踪器的代理实现喵~
- 如果 Diet 模组不存在，使用空实现，不影响核心功能喵~

### Effects System

`common/effect/` 包定义了自定义药水效果：

- **FastDigestionEffect**: 助消化效果，加快食物消化速度喵~
- **StrongStomachEffect**: 铁胃效果，增加胃容量喵~
- **OverstuffedEffect**: 吃撑效果，胃中食物过多时触发喵~
- **VomitingEffect**: 呕吐效果，清空胃中的食物喵~

### Initialization System

`common/init/` 包包含所有注册类（使用 HRR 前缀避免命名冲突）：

- **HRRAttributes**: 属性注册（如额外胃容量）喵~
- **HRRItems**: 物品注册喵~
- **HRRMobEffects**: 药水效果注册喵~
- **HRRPotions**: 药水注册喵~
- **HRRAttachmentTypes**: 数据附件类型注册喵~
- **HRRItemTags**: 物品标签定义喵~

### Mixin System

`mixin/` 包使用 Mixin 修改原版行为：

- **MixinPlayer**: 修改玩家食物消耗逻辑，拦截 `eat()` 方法喵~
- **MixinLivingEntity**: 修改生物实体食物消费逻辑喵~
- **MixinFoodProperties**: 修改食物属性以支持胃系统喵~
- **MixinCakeBlock**: 修复蛋糕相关的 Bug 喵~

Mixin 配置文件位于 `src/main/resources/hunger_reworked_reforged.mixins.json` 喵~

### Client-Server Communication

`network/` 包处理客户端-服务端通信：

- **ClientboundStomachPacket**: 用于同步玩家胃数据到客户端喵~
- **IHRRPacket**: 网络包接口定义喵~

`client/overlay/` 包提供客户端 UI：

- **StomachOverlay**: 在 HUD 上显示胃容量信息喵~

## Development Notes

### Java Version
项目使用 Java 21，构建工具链配置在 `build.gradle` 中喵~

### Testing Framework
- 使用 JUnit 5 (Jupiter) 进行单元测试喵~
- 使用 Mockito 进行 Mock 测试喵~
- 测试配置支持并行执行（`maxParallelForks = Runtime.runtime.availableProcessors() - 1`）喵~
- 游戏测试使用 NeoForge 的 Game Test 框架，配置了 JaCoCo agent 收集覆盖率喵~

### Code Coverage
项目配置了 JaCoCo 用于代码覆盖率分析：
- 单元测试覆盖率：通过 `test` 任务收集喵~
- 游戏测试覆盖率：通过 `gameTestServer` 任务收集（配置了 JaCoCo agent）喵~
- CI 流程中会自动上传覆盖率报告到 Codecov 喵~

### Configuration System
模组配置位于 `common/config/Configuration.java`：
- **FAST_DIGEST_BASE_TICK_RATE**: 快速消化效果的基础 Tick 速率（默认 1，范围 [1, 1200]）喵~
- **FAST_DIGEST_CONSUME_RATE**: 快速消化效果的消耗速率（默认 0.1，范围 [0.001, 1200]）喵~

配置文件会在游戏运行时生成在 `config/hunger_reworked_reforged-common.toml` 喵~

### Item Tag System
模组定义了自定义物品标签：
- `hunger_reworked_reforged:ignore_stomach`: 标记的食物会恢复原版效果，不使用胃系统喵~

### Data Generation
模组使用 NeoForge 的数据生成系统：
- 生成的资源位于 `src/generated/resources/` 喵~
- 必须先运行 `./gradlew runData` 才能构建项目喵~

## CI/CD

GitHub Actions 配置位于 `.github/workflows/build.yml`：
1. 缓存 Gradle 依赖和生成数据喵~
2. 执行数据生成（`runData`）喵~
3. 构建项目（`build`）喵~
4. 运行游戏测试服务器（`runGameTestServer`）喵~
5. 生成覆盖率报告并上传到 Codecov 喵~
6. 上传构建产物（JAR 和 sources JAR）喵~