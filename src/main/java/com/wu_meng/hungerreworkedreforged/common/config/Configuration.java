package com.wu_meng.hungerreworkedreforged.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 模组配置类，定义了所有可配置的参数喵~
 *
 * @author liudongyu
 */
public class Configuration {
	/**
	 * 通用配置规范喵~
	 */
	public static final ModConfigSpec COMMON_CONFIG;

	/**
	 * 快速消化效果的基础 Tick 速率，默认为 1 tick，范围 [1, 1200] 喵~
	 */
	public static final ModConfigSpec.IntValue FAST_DIGEST_BASE_TICK_RATE;

	/**
	 * 快速消化效果的消耗速率，默认为 0.1，范围 [0.001, 1200] 喵~
	 */
	public static final ModConfigSpec.DoubleValue FAST_DIGEST_CONSUME_RATE;

	/** 助消化效果是否能在饥饿值已满时继续推进胃内容。 */
	public static final ModConfigSpec.BooleanValue FAST_DIGEST_WHEN_FULL;

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("fast_digestion_setting");
        FAST_DIGEST_BASE_TICK_RATE = builder.defineInRange("FAST_DIGEST_BASE_TICK_RATE", 1, 1, 1200);
        FAST_DIGEST_CONSUME_RATE = builder.defineInRange("FAST_DIGEST_CONSUME_RATE", 0.1, 0.001, 1200);
		FAST_DIGEST_WHEN_FULL = builder.comment(
				"Whether Fast Digestion keeps emptying the stomach while the hunger bar is full.")
				.define("FAST_DIGEST_WHEN_FULL", true);
        builder.pop();
        COMMON_CONFIG = builder.build();
    }
}
