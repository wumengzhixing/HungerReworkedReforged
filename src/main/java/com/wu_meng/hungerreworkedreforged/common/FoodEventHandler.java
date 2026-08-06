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
package com.wu_meng.hungerreworkedreforged.common;

import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import com.wu_meng.hungerreworkedreforged.common.attachment.LegacyStomachMigrator;
import com.wu_meng.hungerreworkedreforged.common.config.Configuration;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttributes;
import com.wu_meng.hungerreworkedreforged.common.init.HRRMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.io.File;

/**
 * 食物系统事件处理器，负责处理玩家相关的食物和胃系统事件喵~
 *
 * @author liudongyu
 */
public final class FoodEventHandler {
	/** 在原版玩家 NBT 已载入后，迁移 1.18.2 的 Forge capability 数据。 */
	@SubscribeEvent
	public static void onPlayerDataLoaded(PlayerEvent.LoadFromFile event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			File playerFile = new File(event.getPlayerDirectory(), event.getPlayerUUID() + ".dat");
			LegacyStomachMigrator.migrateFromPlayerFile(player, playerFile.toPath());
		}
	}

    /**
     * 玩家克隆事件处理器，用于在玩家重生或从末地返回时保留胃数据喵~
     * <p>
     * 当玩家不是因为死亡而被克隆时（例如从末地返回），将原玩家的胃数据复制到新玩家实体喵~
     * </p>
     *
     * @param event 玩家克隆事件喵~
     */
    @SubscribeEvent
	public static void onPlayerCloned(PlayerEvent.Clone event) {
		if (!event.isWasDeath()) {
			PlayerStomach stomach = event.getOriginal().getData(HRRAttachmentTypes.PLAYER_STOMACH);
			event.getEntity().setData(HRRAttachmentTypes.PLAYER_STOMACH, new PlayerStomach(stomach));
		}
	}

    /**
     * 玩家登录事件处理器，在玩家加入服务器时同步胃数据到客户端喵~
     *
     * @param event 玩家登录事件喵~
     */
    @SubscribeEvent
	public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			PlayerStomach.sendStomachUpdatePacket(player);
		}
	}

	/** 玩家重生后重新同步胃容量，避免客户端保留死亡前的覆盖层数据。 */
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			PlayerStomach.sendStomachUpdatePacket(player);
		}
	}

	/** 玩家切换维度后重新同步胃数据。 */
	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			PlayerStomach.sendStomachUpdatePacket(player);
		}
	}

    /**
     * 玩家 Tick 事件处理器，负责定期消化胃中的食物喵~
     * <p>
     * 每 60 tick（3 秒）执行一次普通消化，消化速率为 0.1 喵~<br>
     * 如果玩家拥有快速消化效果，则按配置的频率额外执行快速消化喵~
     * </p>
     *
     * @param event 玩家 Tick 事件喵~
     */
    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof ServerPlayer player) || player.getAbilities().instabuild) {
			return;
		}

		double digestAmount = 0;
		boolean fastDigestionTriggered = false;
		if (player.tickCount % 60 == 0) {
			digestAmount += player.getAttributeValue(HRRAttributes.DIGESTION_RATE);
		}

		MobEffectInstance effect = player.getEffect(HRRMobEffects.FAST_DIGESTION);
		if (effect != null && player.tickCount % Configuration.FAST_DIGEST_BASE_TICK_RATE.get() == 0) {
			digestAmount += (effect.getAmplifier() + 1) * Configuration.FAST_DIGEST_CONSUME_RATE.get();
			fastDigestionTriggered = true;
		}

		if (digestAmount > 0) {
			PlayerStomach stomach = player.getData(HRRAttachmentTypes.PLAYER_STOMACH);
			stomach.digest(player, digestAmount,
					fastDigestionTriggered && Configuration.FAST_DIGEST_WHEN_FULL.get());
		}
    }

    private FoodEventHandler() {
    }
}
