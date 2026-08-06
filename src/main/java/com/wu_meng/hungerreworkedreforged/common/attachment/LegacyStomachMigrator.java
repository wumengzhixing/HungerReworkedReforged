package com.wu_meng.hungerreworkedreforged.common.attachment;

import com.wu_meng.hungerreworkedreforged.common.HRRLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 将 1.18.2 Forge capability 中的胃内容一次性迁移到 1.21.1 Attachment。 */
public final class LegacyStomachMigrator {
	private static final String LEGACY_CAPS_KEY = "ForgeCaps";
	private static final String LEGACY_STOMACH_KEY = "hunger_reworked_reforged:player_stomach";
	private static final int MINECRAFT_1_18_2_DATA_VERSION = 2975;
	private static final long MAX_PLAYER_FILE_BYTES = 16L * 1024L * 1024L;

	/**
	 * 读取玩家原始 dat 文件中的旧 capability。只有当前 Attachment 为空时才会迁移，避免覆盖新数据。
	 */
	public static boolean migrateFromPlayerFile(ServerPlayer player, Path playerFile) {
		if (!player.getData(com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes.PLAYER_STOMACH)
				.isEmpty() || !Files.isRegularFile(playerFile)) {
			return false;
		}

		try {
			CompoundTag root = NbtIo.readCompressed(playerFile, NbtAccounter.create(MAX_PLAYER_FILE_BYTES));
			if (!root.contains(LEGACY_CAPS_KEY, Tag.TAG_COMPOUND)) {
				return false;
			}
			CompoundTag forgeCaps = root.getCompound(LEGACY_CAPS_KEY);
			if (!forgeCaps.contains(LEGACY_STOMACH_KEY, Tag.TAG_COMPOUND)) {
				return false;
			}

			CompoundTag legacyStomach = forgeCaps.getCompound(LEGACY_STOMACH_KEY);
			ListTag legacyFoods = legacyStomach.getList("content", Tag.TAG_COMPOUND);
			if (legacyFoods.isEmpty()) {
				return false;
			}

			int dataVersion = root.contains("DataVersion", Tag.TAG_ANY_NUMERIC)
					? root.getInt("DataVersion") : MINECRAFT_1_18_2_DATA_VERSION;
			List<PlayerStomach.Food> foods = readLegacyFoods(player, legacyFoods, dataVersion);
			if (foods.isEmpty()) {
				return false;
			}

			player.setData(
					com.wu_meng.hungerreworkedreforged.common.init.HRRAttachmentTypes.PLAYER_STOMACH,
					new PlayerStomach(foods)
			);
			HRRLogger.info("Migrated " + foods.size() + " stomach entries from the 1.18.2 player capability for " +
					player.getGameProfile().getName() + ".");
			return true;
		} catch (IOException | RuntimeException e) {
			HRRLogger.warn("Failed to migrate the legacy stomach data for " +
					player.getGameProfile().getName() + ". The new attachment was left unchanged.", e);
			return false;
		}
	}

	static List<PlayerStomach.Food> readLegacyFoods(ServerPlayer player, ListTag legacyFoods, int dataVersion) {
		List<ItemStack> stacks = updateLegacyItemStacks(player, legacyFoods, dataVersion);
		List<PlayerStomach.Food> result = new ArrayList<>();
		int limit = Math.min(legacyFoods.size(), PlayerStomach.MAX_CONTENT_ENTRIES);
		for (int i = 0; i < limit; i++) {
			CompoundTag legacyFood = legacyFoods.getCompound(i);
			int food = legacyFood.getInt("food");
			float saturation = legacyFood.getFloat("sat");
			double progress = Math.clamp(legacyFood.getDouble("prog"), 0.0D, 1.0D);
			if (food <= 0 || food > PlayerStomach.MAX_TOTAL_FOOD) {
				continue;
			}

			List<FoodProperties.PossibleEffect> effects = readLegacyEffects(
					legacyFood.getList("effects", Tag.TAG_COMPOUND));
			ItemStack stack = i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY;
			// 1.18 同一个 sat 字段既控制消化权重，也直接作为整份食物的饱和度增量。
			result.add(new PlayerStomach.Food(stack, food, Math.max(0, saturation), Math.max(0, saturation),
					progress, effects));
		}
		return result;
	}

	private static List<ItemStack> updateLegacyItemStacks(ServerPlayer player, ListTag legacyFoods, int dataVersion) {
		CompoundTag fixture = new CompoundTag();
		ListTag inventory = new ListTag();
		for (int i = 0; i < legacyFoods.size(); i++) {
			CompoundTag stack = legacyFoods.getCompound(i).getCompound("stack").copy();
			stack.putByte("Slot", (byte)i);
			inventory.add(stack);
		}
		fixture.put("Inventory", inventory);

		CompoundTag updated = DataFixTypes.PLAYER.updateToCurrentVersion(
				DataFixers.getDataFixer(), fixture, Math.max(0, dataVersion));
		ListTag updatedInventory = updated.getList("Inventory", Tag.TAG_COMPOUND);
		ItemStack[] result = new ItemStack[legacyFoods.size()];
		Arrays.fill(result, ItemStack.EMPTY);
		for (int i = 0; i < updatedInventory.size(); i++) {
			CompoundTag stackTag = updatedInventory.getCompound(i);
			int index = Byte.toUnsignedInt(stackTag.getByte("Slot"));
			// 对超过 256 项的恶意旧存档按列表位置回退；正常玩家不会接近该数量。
			if (index >= result.length || (index < i && i < result.length)) {
				index = i;
			}
			if (index < result.length) {
				result[index] = ItemStack.parseOptional(player.registryAccess(), stackTag);
			}
		}
		return List.of(result);
	}

	static List<FoodProperties.PossibleEffect> readLegacyEffects(ListTag legacyEffects) {
		List<FoodProperties.PossibleEffect> effects = new ArrayList<>();
		for (int i = 0; i < legacyEffects.size(); i++) {
			CompoundTag tag = legacyEffects.getCompound(i);
			MobEffectInstance effect = readLegacyEffect(tag);
			if (effect == null) {
				continue;
			}
			float probability = Math.clamp(tag.getFloat("prob"), 0.0F, 1.0F);
			effects.add(new FoodProperties.PossibleEffect(() -> new MobEffectInstance(effect), probability));
		}
		return List.copyOf(effects);
	}

	private static MobEffectInstance readLegacyEffect(CompoundTag tag) {
		int rawId = Byte.toUnsignedInt(tag.getByte("Id"));
		var holder = BuiltInRegistries.MOB_EFFECT.getHolder(rawId).orElse(null);
		if (holder == null) {
			return null;
		}
		int amplifier = Byte.toUnsignedInt(tag.getByte("Amplifier"));
		int duration = Math.max(0, tag.getInt("Duration"));
		boolean ambient = tag.getBoolean("Ambient");
		boolean visible = !tag.contains("ShowParticles") || tag.getBoolean("ShowParticles");
		boolean showIcon = !tag.contains("ShowIcon") ? visible : tag.getBoolean("ShowIcon");
		MobEffectInstance hidden = tag.contains("HiddenEffect", Tag.TAG_COMPOUND)
				? readLegacyEffect(tag.getCompound("HiddenEffect")) : null;
		return new MobEffectInstance(holder, duration, amplifier, ambient, visible, showIcon, hidden);
	}

	private LegacyStomachMigrator() {
	}
}
