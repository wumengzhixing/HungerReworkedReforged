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
import com.wu_meng.hungerreworkedreforged.common.effect.FastDigestionEffect;
import com.wu_meng.hungerreworkedreforged.common.effect.OverstuffedEffect;
import com.wu_meng.hungerreworkedreforged.common.effect.StrongStomachEffect;
import com.wu_meng.hungerreworkedreforged.common.effect.VomitingEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 状态效果注册表喵~
 *
 * @author liudongyu
 */
public final class HRRMobEffects {
	private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, HungerReworked.MOD_ID);

	/** 过饱效果：减少 10% 移动速度喵~ */
	public static final DeferredHolder<MobEffect, MobEffect> OVERSTUFFED = REGISTER.register("overstuffed",
			() -> new OverstuffedEffect(MobEffectCategory.HARMFUL, 0xb57b18)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED,
							ResourceLocation.fromNamespaceAndPath(HungerReworked.MOD_ID, "effect.overstuffed"),
							-0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

	/** 呕吐效果：减少 30% 移动速度和攻击速度喵~ */
	public static final DeferredHolder<MobEffect, MobEffect> VOMITING = REGISTER.register("vomiting",
			() -> new VomitingEffect(MobEffectCategory.HARMFUL, 0x719665)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED,
							ResourceLocation.fromNamespaceAndPath(HungerReworked.MOD_ID, "effect.vomiting.speed"),
							-0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
					.addAttributeModifier(Attributes.ATTACK_SPEED,
							ResourceLocation.fromNamespaceAndPath(HungerReworked.MOD_ID, "effect.vomiting.attack_speed"),
							-0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

	/** 铁胃效果：增加胃容量喵~ */
	public static final DeferredHolder<MobEffect, MobEffect> STRONG_STOMACH = REGISTER.register("strong_stomach",
			() -> new StrongStomachEffect(MobEffectCategory.BENEFICIAL, 0xd9d9d9));

	/** 快速消化效果：加速消化速度喵~ */
	public static final DeferredHolder<MobEffect, MobEffect> FAST_DIGESTION = REGISTER.register("fast_digestion",
			() -> new FastDigestionEffect(MobEffectCategory.BENEFICIAL, 0xd9d9d9));

	/**
	 * 将状态效果注册表绑定到模组事件总线喵~
	 *
	 * @param modBus 模组事件总线喵~
	 */
	public static void init(IEventBus modBus) {
		REGISTER.register(modBus);
	}

	/**
	 * 将 DeferredHolder 转换为 Holder 以便在 MobEffectInstance 中使用喵~
	 *
	 * @param holder DeferredHolder 喵~
	 * @return 对应的 Holder 喵~
	 */
	public static Holder<MobEffect> holder(DeferredHolder<MobEffect, MobEffect> holder) {
		return holder;
	}

	private HRRMobEffects() {
	}
}