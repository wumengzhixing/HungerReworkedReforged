/*
MIT License

        Copyright (c) 2022 Adneths

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
 */
package com.wu_meng.hungerreworkedreforged.common.diet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Diet 追踪器代理接口喵~
 *
 * @author liudongyu
 */
public interface ProxyIDietTracker {
	/**
	 * 消耗食物并更新玩家的饮食数据喵~
	 * <br/>
	 * 该方法会根据食物物品所属的食物组，更新玩家对应食物组的营养值喵~
	 * <br/>
	 * <br/>
	 * 注意事项：
	 * <br/>
	 *   - 该方法应在服务端调用，客户端调用可能不会同步数据喵~
	 * <br/>
	 *   - {@code healing} 和 {@code saturationModifier} 参数应与食物的原版属性一致喵~
	 * <br/>
	 * <br/>
	 * @param stack					要消耗的食物物品堆栈，不能为 {@code null} 喵~
	 * @param healing				食物提供的饥饿值恢复量（原版食物属性）喵~
	 * @param saturationModifier	食物提供的饱和度修正值（原版食物属性）喵~
	 */
	void consume(ItemStack stack, int healing, float saturationModifier);

	/**
	 * 消耗一口方块食物（例如蛋糕）并更新营养值。
	 */
	void consumeBlock(ResourceLocation blockId);

	/**
	 * 获取指定食物组的当前营养值喵~
	 * <br/>
	 * 营养值通常是一个 0 到 1 之间的浮点数，表示玩家在该食物组中的摄入程度喵~
	 * <br/>
	 * <br/>
	 * @param group	食物组名称，例如 "proteins"、"fruits"、"vegetables" 等，不能为 {@code null} 喵~
	 * @return 该食物组的当前营养值，范围通常为 [0.0, 1.0] 喵~
	 */
	float getValue(String group);

	/**
	 * 设置指定食物组的营养值喵~
	 * <br/>
	 * 该方法允许直接修改玩家在某个食物组中的营养值，通常用于调试、管理员命令或特殊游戏机制喵~
	 * <br/>
	 * <br/>
	 * 注意事项：
	 * <br/>
	 *   - 该方法应在服务端调用，客户端调用可能不会同步数据喵~
	 * <br/>
	 *   - {@code amount} 的有效范围通常为 [0.0, 1.0]，超出范围的值可能会被截断或导致未定义行为喵~
	 * <br/>
	 * <br/>
	 * @param group		食物组名称，例如 "proteins"、"fruits"、"vegetables" 等，不能为 {@code null} 喵~
	 * @param amount	要设置的营养值，通常应在 [0.0, 1.0] 范围内喵~
	 */
	void setValue(String group, float amount);

	/** 将服务端营养数据同步到客户端。 */
	void sync();
}
