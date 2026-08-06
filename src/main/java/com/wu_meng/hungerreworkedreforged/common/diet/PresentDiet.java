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

import net.appleseed.appleseed.api.hook.DietHookRegistry;
import net.appleseed.appleseed.api.query.DietQuery;
import net.appleseed.appleseed.common.capability.DietData;
import net.appleseed.appleseed.common.data.group.DietGroups;
import net.appleseed.appleseed.compat.SandwichCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Diet 模组的具体实现类喵~
 * <br/>
 * 当 Diet 模组存在时，该类提供完整的 Diet 集成功能，将 Diet API 调用包装为代理接口喵~
 * <br/>
 * 该类继承自 {@link ProxyDiet}，覆盖了默认的空实现，通过调用 Diet 模组的实际 API 来提供功能喵~
 * <br/>
 * <br/>
 * 核心功能：
 * <br/>
 * 通过 {@link #get(Player)} 方法，从 Diet Capability 系统获取玩家的饮食追踪器并包装为 {@link ProxyIDietTracker} 喵~
 * <br/>
 * <br/>
 * 使用场景：
 * <br/>
 *   - 在 Diet 模组存在时，作为 {@link ProxyDiet} 的实例使用喵~
 * <br/>
 *   - 在模组初始化阶段，根据 Diet 模组的可用性选择实例化该类或使用默认的 {@link ProxyDiet} 喵~
 * <br/>
 * <br/>
 * @see ProxyDiet
 * @see ProxyIDietTracker
 * @see ProxyIDietGroup
 * @see DietGroups
 * @author liudongyu
 */
public class PresentDiet extends ProxyDiet {
	private static final Map<Class<?>, Boolean> BASE_CAKE_INTERACTION = new ConcurrentHashMap<>();

	@Override
	public boolean shouldDeferBlockFood(Block block) {
		return block instanceof CakeBlock && BASE_CAKE_INTERACTION.computeIfAbsent(
				block.getClass(), PresentDiet::usesBaseCakeInteraction);
	}

	/**
	 * 仅自动接管继承 CakeBlock 原版空手交互的方块。覆盖该入口的模组蛋糕继续由 AppleSeed 即时处理，
	 * 即使覆盖方法内部调用 super，也不会再由 HRR 重复结算营养。
	 */
	private static boolean usesBaseCakeInteraction(Class<?> blockClass) {
		Class<?> current = blockClass;
		while (current != null && CakeBlock.class.isAssignableFrom(current)) {
			try {
				current.getDeclaredMethod("useWithoutItem", BlockState.class, Level.class, BlockPos.class,
						Player.class, BlockHitResult.class);
				return current == CakeBlock.class;
			} catch (NoSuchMethodException ignored) {
				current = current.getSuperclass();
			}
		}
		return false;
	}

	@Override
	public boolean shouldProcessBlockFood(Player player, ResourceLocation blockId, BlockPos pos) {
		var query = DietQuery.getInstance();
		if (query == null) {
			return false;
		}
		return BuiltInRegistries.BLOCK.getOptional(blockId)
				.filter(query::hasBlockNutritionData)
				.filter(block -> DietHookRegistry.shouldProcessBlockFood(player, block, pos))
				.isPresent();
	}

	@Override
	public Set<ProxyIDietGroup> getGroups() {
		return DietGroups.SERVER.getGroups().stream()
				.map(PresentIDietGroup::new)
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * {@inheritDoc}
	 * <br/>
	 * 该方法从 Diet 的 Capability 系统中获取玩家的饮食追踪器喵~
	 * <br/>
	 * @param player	要查询的玩家，不能为 {@code null} 喵~
	 * @return 玩家的饮食追踪器代理，如果获取失败则返回 {@code null} 喵~
	 */
	@Override
	public ProxyIDietTracker get(Player player) {
		return new ProxyIDietTracker() {
			@Override
			public void consume(ItemStack stack, int healing, float saturationModifier) {
				if (stack.isEmpty()) {
					return;
				}
				Map<String, Float> gains = new LinkedHashMap<>();
				if (DietHookRegistry.shouldInterceptItemFood(player, stack)) {
					gains = DietHookRegistry.modifyItemFoodGains(player, stack, gains);
				} else if (SandwichCompat.isSandwich(stack)) {
					gains = SandwichCompat.calculateNutrition(stack, player.level());
					gains = DietHookRegistry.modifyItemFoodGains(player, stack, gains);
				} else {
					gains = new LinkedHashMap<>(DietQuery.getInstance().getNutritions(stack.getItem()));
					gains.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue() <= 0);
					gains = DietHookRegistry.modifyItemFoodGains(player, stack, gains);
				}
				applyGains(gains);
				DietHookRegistry.onAfterItemFoodEat(player, stack);
			}

			@Override
			public void consumeBlock(ResourceLocation blockId) {
				BuiltInRegistries.BLOCK.getOptional(blockId).ifPresent(block -> {
					var query = DietQuery.getInstance();
					if (query == null || !query.hasBlockNutritionData(block)) {
						return;
					}
					int bites = Math.max(1, query.getBlockBites(block));
					Map<String, Float> gains = new HashMap<>();
					query.getBlockNutritions(block).forEach((group, value) -> {
						if (value > 0) {
							gains.put(group, value / bites);
						}
					});
					applyGains(DietHookRegistry.modifyBlockFoodGains(player, block, gains));
					DietHookRegistry.onAfterBlockFoodEat(player, block);
				});
			}

			@Override
			public float getValue(String group) {
				return DietData.getValue(player, group);
			}

			@Override
			public void setValue(String group, float amount) {
				DietData.setValue(player, group, amount);
			}

			@Override
			public void sync() {
				// 自动化测试与部分模组的假玩家没有网络连接；数据仍应在服务端正常结算。
				if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.connection != null) {
					DietData.syncToClient(player);
				}
			}

			private void applyGains(Map<String, Float> gains) {
				gains.forEach((group, value) -> {
					if (value > 0) {
						DietData.addValue(player, group, value);
					}
				});
			}
		};
	}
}
