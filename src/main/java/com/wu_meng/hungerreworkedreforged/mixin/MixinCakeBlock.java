package com.wu_meng.hungerreworkedreforged.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wu_meng.hungerreworkedreforged.common.CommonSide;
import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * CakeBlock 的 Mixin 类，修改蛋糕食用行为，使其进入胃系统喵~
 *
 * @author liudongyu
 */
@Mixin(CakeBlock.class)
public class MixinCakeBlock {
	/**
	 * 包装 eat 方法中的 FoodData#eat 调用，使生存玩家吃下的蛋糕进入胃系统喵~
	 *
	 * @param instance FoodData 实例喵~
	 * @param foodLevelModifier 食物值修改量喵~
	 * @param saturationLevelModifier 饱和度修改量喵~
	 * @param original 原始方法喵~
	 */
	@WrapOperation(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"), require = 1)
	private static void hrr$storeCakeInStomach(FoodData instance, int foodLevelModifier,
			float saturationLevelModifier, Operation<Void> original,
			@Local(argsOnly = true) BlockPos pos,
			@Local(argsOnly = true) BlockState state,
			@Local(argsOnly = true) Player player) {
		if (!player.getAbilities().instabuild && !player.isSpectator()) {
			// CakeBlock#eat 同时在客户端执行；客户端必须禁止原版饥饿值预测，等待服务端同步。
			if (!(player instanceof ServerPlayer serverPlayer)) {
				return;
			}

			PlayerStomach stomach = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
			FoodProperties properties = new FoodProperties.Builder().nutrition(foodLevelModifier)
					.saturationModifier(saturationLevelModifier).build();
			var blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
			boolean deferDiet = CommonSide.DIET_PROXY.shouldDeferBlockFood(state.getBlock()) &&
					CommonSide.DIET_PROXY.shouldProcessBlockFood(player, blockId, pos);
			PlayerStomach.Food food = deferDiet
					? new PlayerStomach.Food(blockId, properties)
					: new PlayerStomach.Food(properties);
			if (!stomach.addFood(serverPlayer, food)) {
				original.call(instance, foodLevelModifier, saturationLevelModifier);
			}
			return;
		}
		original.call(instance, foodLevelModifier, saturationLevelModifier);
	}

	/**
	 * 包装 eat 方法中的 canEat 调用，使蛋糕始终可以食用喵~
	 *
	 * @param instance 玩家实例喵~
	 * @param canAlwaysEat 是否总是可以食用喵~
	 * @param original 原始方法喵~
	 * @return 是否可以食用喵~
	 */
	@WrapOperation(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canEat(Z)Z"), require = 1)
	private static boolean hrr$alwaysEdible(Player instance, boolean canAlwaysEat, Operation<Boolean> original) {
		if (!instance.getAbilities().instabuild && !instance.isSpectator()) {
			PlayerStomach stomach = instance.getData(HRRAttachmentTypes.PLAYER_STOMACH);
			return stomach.canAcceptNutrition(2) || original.call(instance, canAlwaysEat);
		}
		return original.call(instance, canAlwaysEat);
	}
}
