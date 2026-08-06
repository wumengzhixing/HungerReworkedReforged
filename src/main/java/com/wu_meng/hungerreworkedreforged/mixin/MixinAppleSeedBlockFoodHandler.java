package com.wu_meng.hungerreworkedreforged.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wu_meng.hungerreworkedreforged.common.CommonSide;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** 只在 AppleSeed 的方块食物事件入口处延迟使用原版交互路径的蛋糕营养。 */
@Pseudo
@Mixin(targets = "net.appleseed.appleseed.common.event.BlockFoodEventHandler", remap = false)
public abstract class MixinAppleSeedBlockFoodHandler {
	@WrapOperation(
			method = "onRightClickBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/appleseed/appleseed/api/hook/DietHookRegistry;shouldProcessBlockFood(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;)Z",
					remap = false
			),
			require = 1
	)
	private static boolean hrr$deferSupportedCake(Player player, Block block, BlockPos pos,
			Operation<Boolean> original) {
		if (CommonSide.isDietIntegrationActive()
				&& CommonSide.DIET_PROXY.shouldDeferBlockFood(block)
				&& player.getData(HRRAttachmentTypes.PLAYER_STOMACH).canAcceptNutrition(2)) {
			return false;
		}
		return original.call(player, block, pos);
	}
}
