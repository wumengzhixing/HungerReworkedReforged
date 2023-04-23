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
package net.mcbbs.uid1525632.hungerreworkedreforged.init;

import java.lang.reflect.InvocationTargetException;

import net.mcbbs.uid1525632.hungerreworkedreforged.core.mixin.MixinFoodProperties;
import net.mcbbs.uid1525632.hungerreworkedreforged.event.FoodEventHandler;
import net.mcbbs.uid1525632.hungerreworkedreforged.integration.diet.ProxyDiet;
import net.mcbbs.uid1525632.hungerreworkedreforged.network.Messages;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class CommonSide
{	
	public static ProxyDiet dietProxy;
	
	@SubscribeEvent
	public static void init(final FMLCommonSetupEvent event)
	{
		Messages.register();
		event.enqueueWork(() -> {
			BrewingRecipeRegistry.addRecipe(NBTIngredient.of(getPotion(Potions.AWKWARD)), Ingredient.of(Items.CALCITE, Items.NAUTILUS_SHELL), getPotion(Registration.STRONG_STOMACH_POTION.get()));
			BrewingRecipeRegistry.addRecipe(NBTIngredient.of(getPotion(Registration.STRONG_STOMACH_POTION.get())), Ingredient.of(Items.REDSTONE), getPotion(Registration.STRONG_STOMACH_POTION_LONG.get()));
			BrewingRecipeRegistry.addRecipe(NBTIngredient.of(getPotion(Registration.STRONG_STOMACH_POTION.get())), Ingredient.of(Items.GLOWSTONE_DUST), getPotion(Registration.STRONG_STOMACH_POTION_STRONG.get()));
		});
		MinecraftForge.EVENT_BUS.register(FoodEventHandler.class);
		if(ModList.get().isLoaded("diet"))
		{
			try
			{
				dietProxy = Class.forName("net.mcbbs.uid1525632.hungerreworkedreforged.integration.diet.PresentDiet").asSubclass(ProxyDiet.class).getDeclaredConstructor().newInstance();
			}
			catch(IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			dietProxy = new ProxyDiet() {};
		}
	}
	
	private static ItemStack getPotion(Potion potion)
	{
		return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
	}
}