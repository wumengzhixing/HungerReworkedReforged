package net.mcbbs.uid1525632.hungerreworkedreforged.core.mixin;

import net.mcbbs.uid1525632.hungerreworkedreforged.capability.PlayerStomach;
import net.mcbbs.uid1525632.hungerreworkedreforged.capability.PlayerStomachProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CakeBlock.class)
public class MixinCakeBlock {
    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getFoodData()Lnet/minecraft/world/food/FoodData;"))
    private static FoodData injected$FillStomach(Player instance) {
        var stomach = instance.getCapability(PlayerStomachProvider.PLAYER_STOMACH);
        stomach.ifPresent(cap->{
            cap.addFood(instance, new PlayerStomach.Food(new FoodProperties.Builder()
                    .nutrition(2).saturationMod(0.1F).build()));
        });
        return instance.getFoodData();
    }

    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static void injected$CancelFoodDataOperation(FoodData instance,
                                        int pFoodLevelModifier,
                                        float pSaturationLevelModifier) {}

    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canEat(Z)Z"))
    private static boolean injected$alwaysEdible(Player instance, boolean pCanAlwaysEat) {
        return true;
    }
}
