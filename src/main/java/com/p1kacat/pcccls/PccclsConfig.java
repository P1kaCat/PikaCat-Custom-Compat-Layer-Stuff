package com.p1kacat.pcccls;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class PccclsConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_VIBRANT_CLOUDS_COMPAT = BUILDER
            .comment("Enable runtime compatibility fixes when Vibrant Clouds is the active shaderpack.")
            .define("enableVibrantCloudsCompat", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SIMPLE_CLOUDS_COMPAT = BUILDER
            .comment("Enable extra runtime compatibility hooks when Simple Clouds is loaded.")
            .define("enableSimpleCloudsCompat", true);

    public static final ModConfigSpec.IntValue CHECK_INTERVAL_TICKS = BUILDER
            .comment("How often shader/mod runtime state should be refreshed (in client ticks).")
            .defineInRange("checkIntervalTicks", 60, 20, 1200);

    public static final ModConfigSpec.ConfigValue<String> SHADER_NAME_MATCH = BUILDER
            .comment("Case-insensitive substring used to detect the target shaderpack name.")
            .define("shaderNameMatch", "vibrant-clouds");

    public static final ModConfigSpec.BooleanValue FIX_PHYSICS_STAFF_FOCUS = BUILDER
            .comment("Fix Creative Physics Staff beam focus calculations under Vibrant Clouds.")
            .define("fixPhysicsStaffFocus", true);

    public static final ModConfigSpec.BooleanValue FIX_LEVITITE_SHADER_FLICKER = BUILDER
            .comment("Disable Aeronautics levitite custom shader layer when compat conditions are met.")
            .define("fixLevititeShaderFlicker", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private PccclsConfig() {
    }
}
