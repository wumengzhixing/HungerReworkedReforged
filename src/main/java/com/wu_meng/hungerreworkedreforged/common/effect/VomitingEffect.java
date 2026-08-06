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

import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.Vec3;

/**
 * 呕吐效果：清空胃中的食物，播放音效和粒子效果喵~
 *
 * @author liudongyu
 */
public class VomitingEffect extends MobEffect {
	public VomitingEffect(MobEffectCategory pCategory, int pColor) {
		super(pCategory, pColor);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (!(entity instanceof ServerPlayer player)) {
			return true;
		}
		ServerLevel level = player.serverLevel();
		PlayerStomach data = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);

		if (!data.isEmpty()) {
			// 清空胃中的食物喵~
			data.popFood(player);
		} else {
			FoodData food = player.getFoodData();
			if (food.getFoodLevel() > Math.max(0, 10 - 2 * amplifier)) {
				food.setFoodLevel(food.getFoodLevel() - 1);
			}
		}

		// 胃内容仍按原频率清除，但降低音效与粒子的发送频率，避免持续效果制造网络和听觉噪声。
		if (player.tickCount % 12 == 0) {
			level.playSound(null, player.blockPosition(), SoundEvents.SLIME_SQUISH,
					SoundSource.PLAYERS, 1, 1 + level.getRandom().nextFloat() * 0.2F);
			level.playSound(null, player.blockPosition(), SoundEvents.DROWNED_HURT,
					SoundSource.PLAYERS, 1, 1 + level.getRandom().nextFloat() * 0.2F);
			Vec3 pos = player.getEyePosition();
			level.sendParticles(ParticleTypes.ITEM_SLIME, pos.x, pos.y, pos.z, 5,
					0.08, 0.08, 0.08, 0.02);
		}

		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration % 4 == 0;
	}
}
