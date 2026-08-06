package com.wu_meng.hungerreworkedreforged.common;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.loading.LoadingModList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FoodConsumptionTrackerTest {
	@BeforeAll
	static void bootstrap() {
		LoadingModList.of(List.of(), List.of(), List.of(), List.of(), Map.of());
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void successfulDeferralCanBeObservedAndConsumed() {
		Player player = mock(Player.class);
		when(player.getId()).thenReturn(7);
		ItemStack apple = new ItemStack(Items.APPLE);

		FoodConsumptionTracker.record(player, apple, true);

		assertTrue(FoodConsumptionTracker.isDeferred(player, apple.copy()));
		assertTrue(FoodConsumptionTracker.consumeDeferred(player, apple.copy()));
		assertFalse(FoodConsumptionTracker.isDeferred(player, apple));
	}

	@Test
	void failedWriteClearsPreviousDeferral() {
		Player player = mock(Player.class);
		when(player.getId()).thenReturn(8);
		ItemStack apple = new ItemStack(Items.APPLE);

		FoodConsumptionTracker.record(player, apple, true);
		FoodConsumptionTracker.record(player, apple, false);

		assertFalse(FoodConsumptionTracker.consumeDeferred(player, apple));
	}

	@Test
	void recordDoesNotMatchAnotherPlayerOrItem() {
		Player first = mock(Player.class);
		Player second = mock(Player.class);
		when(first.getId()).thenReturn(1);
		when(second.getId()).thenReturn(2);
		FoodConsumptionTracker.record(first, new ItemStack(Items.APPLE), true);

		assertFalse(FoodConsumptionTracker.isDeferred(second, new ItemStack(Items.APPLE)));
		assertFalse(FoodConsumptionTracker.consumeDeferred(first, new ItemStack(Items.BREAD)));
	}
}
