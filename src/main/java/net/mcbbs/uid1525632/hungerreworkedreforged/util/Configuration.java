package net.mcbbs.uid1525632.hungerreworkedreforged.util;

import net.minecraftforge.common.ForgeConfigSpec;

public class Configuration {

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.IntValue FAST_DIGEST_BASE_TICK_RATE;
    public static ForgeConfigSpec.DoubleValue FAST_DIGEST_CONSUME_RATE;


    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("fast_digestion_setting");
        FAST_DIGEST_BASE_TICK_RATE = builder.defineInRange("FAST_DIGEST_BASE_TICK_RATE", 1, 1, 1200);
        FAST_DIGEST_CONSUME_RATE = builder.defineInRange("FAST_DIGEST_CONSUME_RATE", 0.1, 0.001, 1200);
        builder.pop();
        COMMON_CONFIG = builder.build();
    }
}
