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
package com.wu_meng.hungerreworkedreforged.common.init;

import com.wu_meng.hungerreworkedreforged.HungerReworked;
import com.wu_meng.hungerreworkedreforged.common.item.DigestiveTabletItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品注册表喵~
 *
 * @author liudongyu
 */
public final class HRRItems {
	private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, HungerReworked.MOD_ID);

	/** 健胃消食片：服用后短时间加快胃中食物的消化。 */
	public static final DeferredHolder<Item, Item> DIGESTIVE_TABLET = REGISTER.register(
			"digestive_tablet",
			() -> new DigestiveTabletItem(new Item.Properties().stacksTo(16))
	);

	/**
	 * 将物品注册表绑定到模组事件总线喵~
	 *
	 * @param modBus 模组事件总线喵~
	 */
	public static void init(IEventBus modBus) {
		REGISTER.register(modBus);
		modBus.addListener(HRRItems::addCreativeTabContents);
	}

	private static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
			event.accept(DIGESTIVE_TABLET.get());
		}
	}

	private HRRItems() {
	}
}
