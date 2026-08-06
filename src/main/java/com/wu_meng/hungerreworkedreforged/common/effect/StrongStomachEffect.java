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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 铁胃效果：如果胃容量降低到阈值以下，移除过饱效果喵~
 *
 * @author liudongyu
 */
public class StrongStomachEffect extends MobEffect {
	public StrongStomachEffect(MobEffectCategory pCategory, int pColor) {
		super(pCategory, pColor);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (!(entity instanceof ServerPlayer player)) {
			return true;
		}
		if (this.hasOverstuffedEffect(player)) {
			PlayerStomach data = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
			// 如果胃容量降低到阈值以下，移除过饱效果喵~
			if (data.getTotalFood() - (1 + amplifier) * 8 <= PlayerStomach.getStomachCapability(player)) {
				this.removeOverstuffedEffect(player);
			}
		}
		return true;
	}

	/**
	 * 检查玩家是否有过饱效果喵~
	 * <p>
	 * 此方法被提取出来以便于单元测试喵~
	 * </p>
	 *
	 * @param player 玩家喵~
	 * @return 是否有过饱效果喵~
	 */
	protected boolean hasOverstuffedEffect(ServerPlayer player) {
		return player.hasEffect(com.wu_meng.hungerreworkedreforged.common.init.HRRMobEffects.OVERSTUFFED);
	}

	/**
	 * 移除玩家的过饱效果喵~
	 * <p>
	 * 此方法被提取出来以便于单元测试喵~
	 * </p>
	 *
	 * @param player 玩家喵~
	 */
	protected void removeOverstuffedEffect(ServerPlayer player) {
		player.removeEffect(com.wu_meng.hungerreworkedreforged.common.init.HRRMobEffects.OVERSTUFFED);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration % 10 == 0;
	}
}