package net.mcbbs.uid1525632.hungerreworkedreforged.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.mcbbs.uid1525632.hungerreworkedreforged.capability.PlayerStomach;
import net.mcbbs.uid1525632.hungerreworkedreforged.capability.PlayerStomachProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static net.mcbbs.uid1525632.hungerreworkedreforged.HungerReworked.IGNORE_STOMACH;

@Mixin(Player.class)
public abstract class MixinPlayer
{
	
	@Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V"), require = 1)
	private void redirectEat(FoodData foodData, Item pItem, ItemStack pStack, LivingEntity entity)
	{
		if(pItem.isEdible() && entity instanceof Player player && !pStack.is(IGNORE_STOMACH))
		{
			PlayerStomach stomach = player.getCapability(PlayerStomachProvider.PLAYER_STOMACH).orElse(null);
			if(stomach != null)
				stomach.addFood(player, new PlayerStomach.Food(pStack.copy(), pStack.getFoodProperties(player)));
			else
				foodData.eat(pItem, pStack, entity);
		}
	}

}
