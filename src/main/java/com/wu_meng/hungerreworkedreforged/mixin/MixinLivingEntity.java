package com.wu_meng.hungerreworkedreforged.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wu_meng.hungerreworkedreforged.common.FoodConsumptionTracker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * LivingEntity 的 Mixin 类，修改实体食用食物时的效果应用行为喵~
 *
 * @author liudongyu
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
	/**
	 * 包装 eat 方法中的 addEatEffect 调用，对于玩家且不在忽略列表中的食物，不立即应用效果喵~
	 *
	 * @param instance 实体实例喵~
	 * @param foodProperties 食物属性喵~
	 * @param original 原始方法喵~
	 * @param food 食物物品喵~
	 */
	@WrapOperation(
			method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V"),
			require = 1
	)
	private void hrr$onAddEatEffect(LivingEntity instance, FoodProperties foodProperties, Operation<Void> original, @Local(argsOnly = true) ItemStack food)  {
		if (instance instanceof Player player && FoodConsumptionTracker.isDeferred(player, food)) {
			return;
		}
		original.call(instance, foodProperties);
	}
}
