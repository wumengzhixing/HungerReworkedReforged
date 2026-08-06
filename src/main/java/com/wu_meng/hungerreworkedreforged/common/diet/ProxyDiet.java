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

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Diet 模组的抽象代理类喵~
 * <br/>
 * 该抽象类定义了与 Diet 模组交互的核心接口，使用抽象工厂模式提供可选的 Diet 集成喵~
 * <br/>
 * 通过继承该类并实现具体方法，可以在 Diet 模组存在时提供完整功能，在 Diet 模组不存在时提供空实现或默认行为喵~
 * <br/>
 * <br/>
 * 设计思路：
 * <br/>
 *   - 该类提供了核心方法：{@link #get(Player)} 用于获取玩家的饮食追踪器喵~
 * <br/>
 *   - 默认实现返回 {@code null}，子类可以根据 Diet 模组的可用性覆盖这些方法喵~
 * <br/>
 * <br/>
 * 具体实现类：
 * <br/>
 *   - {@link PresentDiet}：当 Diet 模组存在时，提供完整的 Diet 集成功能喵~
 * <br/>
 *   - 默认实现（未覆盖方法）：当 Diet 模组不存在时，返回空值或空集合喵~
 * <br/>
 * <br/>
 * @see PresentDiet
 * @see ProxyIDietTracker
 * @see ProxyIDietGroup
 * @author liudongyu
 */
public abstract class ProxyDiet {
	/**
	 * 获取当前可用的营养组。Diet/AppleSeed 未安装时返回空集合。
	 */
	public Set<ProxyIDietGroup> getGroups() {
		return Set.of();
	}

	/** 是否应由 HRR 延迟指定方块的 AppleSeed 营养。 */
	public boolean shouldDeferBlockFood(Block block) {
		return false;
	}

	/** 是否应在方块食物完成消化后交给 AppleSeed 结算。 */
	public boolean shouldProcessBlockFood(Player player, ResourceLocation blockId, BlockPos pos) {
		return false;
	}

	/**
	 * 获取指定玩家的饮食追踪器喵~
	 * <br/>
	 * 该方法用于获取玩家的饮食数据管理器，可以用于记录玩家消耗的食物、查询和修改营养值喵~
	 * <br/>
	 * <br/>
	 * 默认实现：
	 * <br/>
	 *   - 返回 {@code null}，表示 Diet 模组不可用或玩家没有饮食追踪器喵~
	 * <br/>
	 * <br/>
	 * 子类实现：
	 * <br/>
	 *   - {@link PresentDiet#get(Player)}：当 Diet 模组存在时，从 Diet API 获取玩家的追踪器并包装为 {@link ProxyIDietTracker} 返回喵~
	 * <br/>
	 * <br/>
	 * 注意事项：
	 * <br/>
	 *   - 该方法可能在任意线程中被调用，实现时应保证线程安全喵~
	 * <br/>
	 *   - 返回 {@code null} 时，调用方应检查并提供适当的回退逻辑喵~
	 * <br/>
	 * <br/>
	 * @param player	要查询的玩家，不能为 {@code null} 喵~
	 * @return 玩家的饮食追踪器代理，如果 Diet 模组不可用或玩家没有追踪器则返回 {@code null} 喵~
	 */
	@Nullable
	public ProxyIDietTracker get(Player player) {
		return null;
	}
}
