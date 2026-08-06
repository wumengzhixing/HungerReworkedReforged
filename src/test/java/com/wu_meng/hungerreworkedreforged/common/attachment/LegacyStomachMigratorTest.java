package com.wu_meng.hungerreworkedreforged.common.attachment;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.fml.loading.LoadingModList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyStomachMigratorTest {
	@BeforeAll
	static void bootstrap() {
		LoadingModList.of(List.of(), List.of(), List.of(), List.of(), Map.of());
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}

	@Test
	void readsLegacyNumericMobEffect() {
		CompoundTag effectTag = new CompoundTag();
		effectTag.putByte("Id", (byte)BuiltInRegistries.MOB_EFFECT.getId(MobEffects.POISON.value()));
		effectTag.putByte("Amplifier", (byte)2);
		effectTag.putInt("Duration", 200);
		effectTag.putFloat("prob", 0.75F);
		ListTag effectsTag = new ListTag();
		effectsTag.add(effectTag);

		List<FoodProperties.PossibleEffect> effects = LegacyStomachMigrator.readLegacyEffects(effectsTag);

		assertEquals(1, effects.size());
		assertTrue(effects.getFirst().effect().is(MobEffects.POISON));
		assertEquals(2, effects.getFirst().effect().getAmplifier());
		assertEquals(200, effects.getFirst().effect().getDuration());
		assertEquals(0.75F, effects.getFirst().probability());
	}
}
