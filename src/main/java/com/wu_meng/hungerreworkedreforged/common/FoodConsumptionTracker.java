package com.wu_meng.hungerreworkedreforged.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 记录当前服务端进食调用是否已经成功写入胃中。
 * <p>
 * Player#eat、LivingEntity#eat 与 NeoForge 的 Finish 事件在同一服务端线程、同一游戏刻内依次执行，
 * 因此只保留最近一次调用即可。记录不持有玩家对象，并在 Finish 事件读取后立即清除；没有安装
 * AppleSeed 时，过期记录也不会匹配下一游戏刻。
 * </p>
 */
public final class FoodConsumptionTracker {
	private static final ThreadLocal<DeferredConsumption> CURRENT = new ThreadLocal<>();

	/** 记录一次成功进入胃的食物；失败的写入会清除上一条记录。 */
	public static void record(Player player, ItemStack stack, boolean deferred) {
		if (!deferred) {
			CURRENT.remove();
			return;
		}
		CURRENT.set(new DeferredConsumption(player.getId(), player.tickCount, stack.copyWithCount(1)));
	}

	/** 判断当前进食调用是否已成功进入胃，但不消费记录。 */
	public static boolean isDeferred(Player player, ItemStack stack) {
		DeferredConsumption current = CURRENT.get();
		return current != null && current.matches(player, stack);
	}

	/** 供进食完成事件读取并清除记录，防止状态泄漏到下一次进食。 */
	public static boolean consumeDeferred(Player player, ItemStack stack) {
		DeferredConsumption current = CURRENT.get();
		CURRENT.remove();
		return current != null && current.matches(player, stack);
	}

	private record DeferredConsumption(int playerId, int tickCount, ItemStack stack) {
		private boolean matches(Player player, ItemStack candidate) {
			return this.playerId == player.getId() && this.tickCount == player.tickCount &&
					ItemStack.isSameItemSameComponents(this.stack, candidate);
		}
	}

	private FoodConsumptionTracker() {
	}
}
