package com.wu_meng.hungerreworkedreforged.common.init;

import com.wu_meng.hungerreworkedreforged.HungerReworked;
import com.wu_meng.hungerreworkedreforged.common.attachment.PlayerStomach;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 附件类型注册表，用于注册玩家数据附件喵~
 *
 * @author liudongyu
 */
public final class HRRAttachmentTypes {
	private static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HungerReworked.MOD_ID);

	/**
	 * 玩家胃数据附件类型，用于存储玩家的胃数据喵~
	 */
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerStomach>> PLAYER_STOMACH = REGISTER.register(
			"player_stomach",
			() -> AttachmentType.builder(() -> new PlayerStomach())
					.serialize(PlayerStomach.CODEC)
					.build()
	);

	public static void init(IEventBus modBus) {
		REGISTER.register(modBus);
	}
}
