package com.wu_meng.hungerreworkedreforged.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** 只让进入胃系统的食物突破原版饥饿值限制，避免全局修改 FoodProperties。 */
@Mixin(Item.class)
public abstract class MixinItem {
	@WrapOperation(
			method = "use",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canEat(Z)Z"),
			require = 1
	)
	private boolean hrr$allowStomachFood(Player player, boolean canAlwaysEat,
			Operation<Boolean> original, @Local ItemStack stack) {
		if (PlayerStomach.isStomachManagedFood(player, stack)) {
			FoodProperties properties = stack.getFoodProperties(player);
			PlayerStomach stomach = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
			if (properties != null && stomach.canAcceptNutrition(properties.nutrition())) {
				return true;
			}
		}
		return original.call(player, canAlwaysEat);
	}
}
