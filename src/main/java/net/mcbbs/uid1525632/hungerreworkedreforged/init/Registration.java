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

import net.mcbbs.uid1525632.hungerreworkedreforged.HungerReworked;
import net.mcbbs.uid1525632.hungerreworkedreforged.effect.FoodMobEffect;
import net.mcbbs.uid1525632.hungerreworkedreforged.util.Configuration;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration
{
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HungerReworked.MOD_ID);
	private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, HungerReworked.MOD_ID);
	private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, HungerReworked.MOD_ID);

	public static void init()
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(bus);
		MOB_EFFECTS.register(bus);
		POTIONS.register(bus);
	}
	
	public static final RegistryObject<MobEffect> OVERSTUFFED = MOB_EFFECTS.register("overstuffed", 
			() -> new FoodMobEffect(MobEffectCategory.HARMFUL, 0xb57b18).addAttributeModifier(Attributes.MOVEMENT_SPEED, "B9DEBEC9-EE6F-4DFD-A4C1-507FB6D700F6", -0.1, AttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<MobEffect> VOMITING = MOB_EFFECTS.register("vomiting", 
			() -> new FoodMobEffect(MobEffectCategory.HARMFUL, 0x719665).addAttributeModifier(Attributes.MOVEMENT_SPEED, "BF8881B2-62C1-45F9-A06F-8FFD9ADE6B01", -0.3, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_SPEED, "EE38244B-B251-4988-A8EF-800F0CFAB727", -0.3, AttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<MobEffect> STRONG_STOMACH = MOB_EFFECTS.register("strong_stomach", 
			() -> new FoodMobEffect(MobEffectCategory.BENEFICIAL, 0xd9d9d9));
	public static final RegistryObject<MobEffect> FAST_DIGESTION = MOB_EFFECTS.register("fast_digestion",
			() -> new FoodMobEffect(MobEffectCategory.BENEFICIAL, 0xd9d9d9));

	public static final RegistryObject<Potion> STRONG_STOMACH_POTION = POTIONS.register("strong_stomach_potion", () -> new Potion("strong_stomach_potion", new MobEffectInstance(STRONG_STOMACH.get(),3600,0)));
	public static final RegistryObject<Potion> STRONG_STOMACH_POTION_LONG = POTIONS.register("strong_stomach_potion_long", () -> new Potion("strong_stomach_potion", new MobEffectInstance(STRONG_STOMACH.get(),9600,0)));
	public static final RegistryObject<Potion> STRONG_STOMACH_POTION_STRONG = POTIONS.register("strong_stomach_potion_strong", () -> new Potion("strong_stomach_potion", new MobEffectInstance(STRONG_STOMACH.get(),3600,1)));
	public static final RegistryObject<Potion> FAST_DIGESTION_POTION = POTIONS.register("fast_digestion_potion", () -> new Potion("fast_digestion_potion", new MobEffectInstance(FAST_DIGESTION.get(), 3600,0)));
	public static final RegistryObject<Potion> FAST_DIGESTION_POTION_LONG = POTIONS.register("fast_digestion_potion_long", () -> new Potion("fast_digestion_potion", new MobEffectInstance(FAST_DIGESTION.get(), 9600,0)));
	public static final RegistryObject<Potion> FAST_DIGESTION_POTION_STRONG = POTIONS.register("fast_digestion_potion_strong", () -> new Potion("fast_digestion_potion", new MobEffectInstance(FAST_DIGESTION.get(), 3600,1)));
}
