# Hunger Reworked Reforged 饥饿改革重铸

面向 Minecraft 1.21.1 / NeoForge 的饥饿与胃部系统模组。食物会先进入胃中，再随时间逐步转化为饥饿值、饱和度和食物效果。

## 主要内容

- 胃容量 HUD、过饱、呕吐、铁胃和助消化效果。
- 普通、长效和强力版本的铁胃药水与助消化药水。
- 健胃消食片：短时间加快胃中食物的消化；饱食度已满时仍可推进消化。
- 健胃消食片配方：苹果、干海带和糖无序合成 4 个。
- 支持通过 `hunger_reworked_reforged:ignore_stomach` 物品标签让指定食物绕过胃系统、按原版立即生效。
- 可选对接 Diet - AppleSeed Edition 3.0.1，在食物完成消化后结算营养。

## AppleSeed 联动

联动目标版本为 `3.0.1-1.21.1`。AppleSeed 及其必需依赖 CloudWorks API 需要由玩家或整合包单独安装；它们不会打包进本模组成品。

## 从 1.18.2 升级

玩家首次载入时会尝试从旧版 `ForgeCaps` 胃数据迁移食物、消化进度和待触发效果。无法解析的旧物品或效果会被安全跳过；升级世界前仍建议先备份存档。

## 配置

通用配置文件可以调整：

- `FAST_DIGEST_BASE_TICK_RATE`：助消化效果的触发间隔。
- `FAST_DIGEST_CONSUME_RATE`：每次触发推进的消化量。
- `FAST_DIGEST_WHEN_FULL`：饱食度已满时是否继续推进胃中食物，默认开启。

## 构建与测试

使用 Java 21，在项目目录运行：

```text
gradlew build
gradlew runGameTestServer
```

构建产物位于 `build/libs`，测试与覆盖率报告位于 `build/reports`。游戏测试会覆盖普通食物延迟消化及 AppleSeed 蛋糕营养延迟结算。
