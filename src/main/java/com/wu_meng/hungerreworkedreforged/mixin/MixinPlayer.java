package com.wu_meng.hungerreworkedreforged.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wu_meng.hungerreworkedreforged.common.FoodConsumptionTracker;
import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Player 的 Mixin 类，修改玩家食用食物的行为，使食物进入胃系统喵~
 *
 * @author liudongyu
 */
@Mixin(Player.class)
public abstract class MixinPlayer {
	/**
	 * 包装 eat 方法中的 FoodData#eat 调用，将食物添加到胃中而不是直接生效喵~
	 *
	 * @param instance FoodData 实例喵~
	 * @param foodProperties 食物属性喵~
	 * @param original 原始方法喵~
	 * @param food 食物物品喵~
	 */
	@WrapOperation(method = "eat", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"
	), require = 1)
	private void hrr$redirectEat(FoodData instance, FoodProperties foodProperties, Operation<Void> original, @Local(argsOnly = true) ItemStack food) {
		if ((Object)this instanceof Player player && PlayerStomach.isStomachManagedFood(player, food)) {
			// 客户端不预测饥饿值，等待服务端的 FoodData 同步，避免短暂显示已经消化。
			if (player instanceof ServerPlayer serverPlayer) {
				PlayerStomach stomach = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
				boolean deferred = stomach.addFood(serverPlayer, new PlayerStomach.Food(food, foodProperties));
				FoodConsumptionTracker.record(player, food, deferred);
				if (!deferred) {
					original.call(instance, foodProperties);
				}
			}
			return;
		}
		if ((Object)this instanceof ServerPlayer player) {
			FoodConsumptionTracker.record(player, food, false);
		}
		original.call(instance, foodProperties);
	}
}
