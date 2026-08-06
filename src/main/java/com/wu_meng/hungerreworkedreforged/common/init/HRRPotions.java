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
package com.wu_meng.hungerreworkedreforged.common.init;

import com.wu_meng.hungerreworkedreforged.HungerReworked;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 药水注册表喵~
 *
 * @author liudongyu
 */
public final class HRRPotions {
	private static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(Registries.POTION, HungerReworked.MOD_ID);

	/** 铁胃药水（普通）喵~ */
	public static final DeferredHolder<Potion, Potion> STRONG_STOMACH_POTION = REGISTER.register("strong_stomach_potion",
			() -> new Potion("strong_stomach_potion", new MobEffectInstance(HRRMobEffects.holder(HRRMobEffects.STRONG_STOMACH), 3600, 0)));

	/** 铁胃药水（延长）喵~ */
	public static final DeferredHolder<Potion, Potion> STRONG_STOMACH_POTION_LONG = REGISTER.register("strong_stomach_potion_long",
			() -> new Potion("strong_stomach_potion_long", new MobEffectInstance(HRRMobEffects.holder(HRRMobEffects.STRONG_STOMACH), 9600, 0)));

	/** 铁胃药水（增强）喵~ */
	public static final DeferredHolder<Potion, Potion> STRONG_STOMACH_POTION_STRONG = REGISTER.register("strong_stomach_potion_strong",
			() -> new Potion("strong_stomach_potion_strong", new MobEffectInstance(HRRMobEffects.holder(HRRMobEffects.STRONG_STOMACH), 3600, 1)));

	/** 快速消化药水（普通）喵~ */
	public static final DeferredHolder<Potion, Potion> FAST_DIGESTION_POTION = REGISTER.register("fast_digestion_potion",
			() -> new Potion("fast_digestion_potion", new MobEffectInstance(HRRMobEffects.holder(HRRMobEffects.FAST_DIGESTION), 3600, 0)));

	/** 快速消化药水（延长）喵~ */
	public static final DeferredHolder<Potion, Potion> FAST_DIGESTION_POTION_LONG = REGISTER.register("fast_digestion_potion_long",
			() -> new Potion("fast_digestion_potion_long", new MobEffectInstance(HRRMobEffects.holder(HRRMobEffects.FAST_DIGESTION), 9600, 0)));

	/** 快速消化药水（增强）喵~ */
	public static final DeferredHolder<Potion, Potion> FAST_DIGESTION_POTION_STRONG = REGISTER.register("fast_digestion_potion_strong",
			() -> new Potion("fast_digestion_potion_strong", new MobEffectInstance(HRRMobEffects.holder(HRRMobEffects.FAST_DIGESTION), 3600, 1)));

	/**
	 * 将药水注册表绑定到模组事件总线喵~
	 *
	 * @param modBus 模组事件总线喵~
	 */
	public static void init(IEventBus modBus) {
		REGISTER.register(modBus);
	}

	private HRRPotions() {
	}
}
