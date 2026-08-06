package com.wu_meng.hungerreworkedreforged.mixin;

import com.wu_meng.hungerreworkedreforged.common.CommonSide;
import com.wu_meng.hungerreworkedreforged.common.FoodConsumptionTracker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在 AppleSeed 3.0.1 的物品营养处理入口延迟整笔结算。
 * 营养、同步和 after-eat Hook 会在食物真正消化完成后由 PresentDiet 一并处理。
 */
@Pseudo
@Mixin(targets = "net.appleseed.appleseed.AppleSeed", remap = false)
public abstract class MixinAppleSeed {
	@Inject(method = "onItemUseFinish", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
	private void hrr$deferItemNutrition(LivingEntityUseItemEvent.Finish event, CallbackInfo ci) {
		LivingEntity entity = event.getEntity();
		if (entity.level().isClientSide() || !(entity instanceof Player player)) {
			return;
		}

		ItemStack stack = event.getItem();
		boolean deferred = FoodConsumptionTracker.consumeDeferred(player, stack);
		if (CommonSide.isDietIntegrationActive() && deferred) {
			ci.cancel();
		}
	}
}
