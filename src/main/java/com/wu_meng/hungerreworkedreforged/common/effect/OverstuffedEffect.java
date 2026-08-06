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
package com.wu_meng.hungerreworkedreforged.common.effect;

import com.wu_meng.hungerreworkedreforged.common.init.HRRMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * 过饱效果：有概率转变为呕吐效果，并附加虚弱和缓慢效果喵~
 *
 * @author liudongyu
 */
public class OverstuffedEffect extends MobEffect {
	public OverstuffedEffect(MobEffectCategory pCategory, int pColor) {
		super(pCategory, pColor);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (!(entity instanceof ServerPlayer)) {
			return true;
		}
		Level level = entity.level();
		if (level.getRandom().nextFloat() < 0.02f * (amplifier + 1)) {
			// 过饱状态有概率转变为呕吐状态喵~
			this.transitionToVomiting(entity, amplifier);
		}
		return true;
	}

	/**
	 * 将过饱状态转变为呕吐状态喵~
	 * <p>
	 * 此方法被提取出来以便于单元测试喵~
	 * </p>
	 *
	 * @param entity 受影响的实体喵~
	 * @param amplifier 效果等级喵~
	 */
	protected void transitionToVomiting(LivingEntity entity, int amplifier) {
		entity.removeEffect(HRRMobEffects.OVERSTUFFED);
		entity.addEffect(new MobEffectInstance(HRRMobEffects.VOMITING, 200, amplifier));
		entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 6000, 0));
		entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6000, 0));
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}
}
