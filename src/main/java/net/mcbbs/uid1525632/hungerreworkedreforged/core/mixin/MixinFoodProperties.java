package net.mcbbs.uid1525632.hungerreworkedreforged.core.mixin;

import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodProperties.class)
public abstract class MixinFoodProperties {
    @Inject(method = "canAlwaysEat", at = @At("HEAD"), cancellable = true)
    void setCanAlwaysEat(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

}
