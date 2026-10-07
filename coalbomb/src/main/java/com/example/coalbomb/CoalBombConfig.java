package com.example.coalbomb;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CoalBombConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue RADIUS;
    public static final ForgeConfigSpec.IntValue FUSE_SECONDS;
    public static final ForgeConfigSpec.IntValue WARN_SECONDS;
    public static final ForgeConfigSpec.IntValue VERTICAL_RANGE;
    public static final ForgeConfigSpec.IntValue START_COLUMNS;
    public static final ForgeConfigSpec.IntValue MAX_COLUMNS_PER_TICK;
    public static final ForgeConfigSpec.DoubleValue ACCELERATION;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("coal_bomb");
        RADIUS = b.comment("Radius of the wave in blocks (50 = an area 100x100 blocks)")
                .defineInRange("radius", 50, 1, 200);
        FUSE_SECONDS = b.comment("Seconds the bomb just glows after landing")
                .defineInRange("fuseSeconds", 55, 1, 600);
        WARN_SECONDS = b.comment("Seconds the area is marked with red particles before the wave starts")
                .defineInRange("warnSeconds", 5, 0, 60);
        VERTICAL_RANGE = b.comment("How many blocks up and down from the bomb are removed")
                .defineInRange("verticalRange", 64, 1, 400);
        START_COLUMNS = b.comment("Columns of blocks removed per tick at the very start of the wave")
                .defineInRange("startColumnsPerTick", 3, 1, 1000);
        ACCELERATION = b.comment("How much the columns-per-tick grows each tick (higher = faster speed-up)")
                .defineInRange("acceleration", 0.5D, 0.0D, 50.0D);
        MAX_COLUMNS_PER_TICK = b.comment("Upper limit of columns removed per tick (lower it if the server lags)")
                .defineInRange("maxColumnsPerTick", 120, 1, 2000);
        b.pop();
        SPEC = b.build();
    }

    private CoalBombConfig() {}
}
