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
package com.wu_meng.hungerreworkedreforged;

import com.wu_meng.hungerreworkedreforged.client.ClientSide;
import com.wu_meng.hungerreworkedreforged.common.CommonSide;
import com.wu_meng.hungerreworkedreforged.common.FoodEventHandler;
import com.wu_meng.hungerreworkedreforged.common.config.Configuration;
import com.wu_meng.hungerreworkedreforged.common.init.*;
import com.wu_meng.hungerreworkedreforged.gametest.HRRGameTests;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

/**
 * Hunger Reworked Reforged 模组主类，负责模组的初始化、配置注册、物品与药水系统注册以及酿造配方注册喵~
 *
 * @author liudongyu
 */
@Mod(HungerReworked.MOD_ID)
public class HungerReworked {
    /**
     * 模组 ID 常量喵~
     */
    public static final String MOD_ID = "hunger_reworked_reforged";

    /**
     * 构造方法，执行模组初始化流程喵~
     * <p>
     * 注册配置、初始化物品、生物效果和药水系统，并注册事件处理器喵~
     * </p>
     *
     * @param modBus 模组事件总线，用于注册模组生命周期事件喵~
     * @param modContainer 模组容器，用于注册配置喵~
     */
    public HungerReworked(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);
        HRRAttributes.init(modBus);
        HRRAttachmentTypes.init(modBus);
        HRRItems.init(modBus);
        HRRMobEffects.init(modBus);
        HRRPotions.init(modBus);
        modBus.register(CommonSide.class);
		modBus.addListener(this::onRegisterGameTests);
		if (FMLEnvironment.dist == Dist.CLIENT) {
			modBus.addListener(ClientSide::onRegisterGuiLayers);
		}
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(FoodEventHandler.class);
    }

	private void onRegisterGameTests(RegisterGameTestsEvent event) {
		event.register(HRRGameTests.class);
	}

    /**
     * 注册酿造配方事件处理器喵~
     * <p>
     * 添加以下酿造配方：
     * <ul>
     *     <li>粗制药水 + 方解石/鹦鹉螺壳 → 铁胃药水喵~</li>
     *     <li>铁胃药水 + 红石 → 长效铁胃药水喵~</li>
     *     <li>铁胃药水 + 荧石粉 → 强化铁胃药水喵~</li>
     * </ul>
     * </p>
     *
     * @param event 酿造配方注册事件喵~
     */
    @SubscribeEvent
	public void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
		PotionBrewing.Builder builder = event.getBuilder();
		builder.addMix(Potions.AWKWARD, Items.CALCITE, HRRPotions.STRONG_STOMACH_POTION);
		builder.addMix(Potions.AWKWARD, Items.NAUTILUS_SHELL, HRRPotions.STRONG_STOMACH_POTION);
		builder.addMix(HRRPotions.STRONG_STOMACH_POTION, Items.REDSTONE, HRRPotions.STRONG_STOMACH_POTION_LONG);
		builder.addMix(HRRPotions.STRONG_STOMACH_POTION, Items.GLOWSTONE_DUST, HRRPotions.STRONG_STOMACH_POTION_STRONG);
		builder.addMix(Potions.AWKWARD, HRRItems.DIGESTIVE_TABLET.get(), HRRPotions.FAST_DIGESTION_POTION);
		builder.addMix(HRRPotions.FAST_DIGESTION_POTION, Items.REDSTONE, HRRPotions.FAST_DIGESTION_POTION_LONG);
		builder.addMix(HRRPotions.FAST_DIGESTION_POTION, Items.GLOWSTONE_DUST, HRRPotions.FAST_DIGESTION_POTION_STRONG);
	}
}
