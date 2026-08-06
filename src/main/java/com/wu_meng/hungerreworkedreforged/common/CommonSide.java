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

import com.wu_meng.hungerreworkedreforged.common.diet.ProxyDiet;
import com.wu_meng.hungerreworkedreforged.common.init.HRRAttributes;
import com.wu_meng.hungerreworkedreforged.network.ClientboundStomachPacket;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * 通用侧（服务端和客户端共用）事件处理类，负责模组初始化、网络注册和实体属性修改喵~
 *
 * @author liudongyu
 */
public final class CommonSide {
	private static volatile boolean dietIntegrationActive;

    /**
     * Diet 模组代理实例，用于与 Diet 模组进行联动喵~
     * <p>
     * 如果 Diet 模组已加载，则为 PresentDiet 实例；否则为空实现的代理喵~
     * </p>
     */
	@SuppressWarnings({"java:S1444", "java:S3008"})
	public static ProxyDiet DIET_PROXY = new ProxyDiet() {
	};

    /**
     * 模组通用初始化事件处理器喵~
     * <p>
     * 检测 Diet 模组是否已加载，并初始化相应的代理实例喵~
     * 如果 Diet 模组已加载但版本不匹配，会记录错误日志喵~
     * </p>
     *
     * @param event 通用初始化事件喵~
     */
    @SubscribeEvent
    public static void init(final FMLCommonSetupEvent event) {
		event.enqueueWork(CommonSide::initializeDietCompatibility);
    }

	private static void initializeDietCompatibility() {
		dietIntegrationActive = false;
		if (!ModList.get().isLoaded("appleseed")) {
			return;
		}
		try {
			DIET_PROXY = Class.forName("com.wu_meng.hungerreworkedreforged.common.diet.PresentDiet")
					.asSubclass(ProxyDiet.class).getDeclaredConstructor().newInstance();
			dietIntegrationActive = true;
			HRRLogger.info("AppleSeed diet compatibility initialized successfully.");
		} catch (ReflectiveOperationException | LinkageError | IllegalArgumentException | SecurityException e) {
			HRRLogger.error("Failed to load AppleSeed diet proxy. AppleSeed will keep its original nutrition handling.", e);
		}
	}

	/** 仅在兼容代理完整初始化后，Mixin 才会接管 AppleSeed 的即时营养结算。 */
	public static boolean isDietIntegrationActive() {
		return dietIntegrationActive;
	}

    /**
     * 注册网络包处理器喵~
     *
     * @param event 网络包处理器注册事件喵~
     */
    @SubscribeEvent
    public static void networkRegistry(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .commonToClient(
                        ClientboundStomachPacket.TYPE,
                        ClientboundStomachPacket.STREAM_CODEC,
                        ClientboundStomachPacket::handle
                );
    }

    /**
     * 修改默认实体属性，为玩家实体添加额外胃容量属性喵~
     *
     * @param event 实体属性修改事件喵~
     */
    @SubscribeEvent
    public static void modifyDefaultAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, HRRAttributes.EXTRA_STOMACH, 0.0D);
        event.add(EntityType.PLAYER, HRRAttributes.DIGESTION_RATE, 0.1D);
    }

    private CommonSide() {
    }
}
