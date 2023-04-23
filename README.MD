# Hunger Reworked Reforged 饥饿改革重铸
## 新增特性

### 助消化效果与对应药水
新增助消化效果，以及对应的`助消化药水`，`强力助消化药水`，`长效助消化药水`。未添加合成表。
不可修改药水时长，推荐整合包制作者自己制作自定义药水。
配置文件中可修改`FAST_DIGEST_BASE_TICK_RATE`与`FAST_DIGEST_CONSUME_RATE`，前者意思是多少Tick效果生效一次，后者意思是效果每次生效消化多少。

### 物品 Tag `hunger_reworked_reforged:ignore_stomach`
该 Tag 包含的食物将不会给予玩家饱腹度，而会恢复原版效果。

### 增加中文翻译

## 杂项

### 改动
* 吃吐了不会再导致 Diet 失去所有的营养值

### Bug修复
* 吃蛋糕会给予额外的记录
* 吃蛋糕重置蛋糕方块两次