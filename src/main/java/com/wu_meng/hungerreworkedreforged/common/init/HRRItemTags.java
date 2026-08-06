package com.wu_meng.hungerreworkedreforged.common.init;

import com.wu_meng.hungerreworkedreforged.HungerReworked;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * 物品标签注册表喵~
 *
 * @author liudongyu
 */
public final class HRRItemTags {
	/**
	 * 忽略胃系统的物品标签，带有此标签的食物会直接生效而不进入胃喵~
	 */
	public static final TagKey<Item> IGNORE_STOMACH = ItemTags.create(ResourceLocation.fromNamespaceAndPath(HungerReworked.MOD_ID, "ignore_stomach"));

	private HRRItemTags() {
	}
}
