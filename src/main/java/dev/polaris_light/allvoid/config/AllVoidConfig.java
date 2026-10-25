package dev.polaris_light.allvoid.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Common settings for the renewable resources added by All Void. */
public final class AllVoidConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue RENEWABLE_NETHERRACK;
    public static final ModConfigSpec.BooleanValue TALL_FLOWERS_FROM_WANDERING_TRADER;
    public static final ModConfigSpec.BooleanValue LIGHTNING_ELECTRIFIES_VINES;
    public static final ModConfigSpec.BooleanValue GATEWAYS_SPAWN_CHORUS;
    public static final ModConfigSpec.BooleanValue HUGE_MUSHROOMS_SPREAD_MYCELIUM;
    public static final ModConfigSpec.BooleanValue SPREADING_SMALL_DRIPLEAVES;
    public static final ModConfigSpec.BooleanValue SPREADING_CORAL;
    public static final ModConfigSpec.BooleanValue RENEWABLE_SPONGES;
    public static final ModConfigSpec.BooleanValue PIGLINS_SPAWNING_IN_BASTIONS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("All Void renewable resource settings").push("features");

        RENEWABLE_NETHERRACK = builder
                .comment("Generate netherrack or nylium beneath newly created Nether portals.")
                .define("renewableNetherrack", true);
        TALL_FLOWERS_FROM_WANDERING_TRADER = builder
                .comment("Add Bedrock-style tall flower trades to wandering traders.")
                .define("tallFlowersFromWanderingTrader", true);
        LIGHTNING_ELECTRIFIES_VINES = builder
                .comment("Turn vines attached to glowstone into glow lichen when lightning strikes.")
                .define("lightningElectrifiesVines", true);
        GATEWAYS_SPAWN_CHORUS = builder
                .comment("Grow a chorus plant on newly generated End Gateway islands.")
                .define("gatewaysSpawnChorus", true);
        HUGE_MUSHROOMS_SPREAD_MYCELIUM = builder
                .comment("Spread mycelium around successfully generated huge mushrooms.")
                .define("hugeMushroomsSpreadMycelium", true);
        SPREADING_SMALL_DRIPLEAVES = builder
                .comment("Allow suitable small dripleaves to propagate on random ticks.")
                .define("spreadingSmallDripleaves", true);
        SPREADING_CORAL = builder
                .comment("Allow calcite surrounded by coral to convert into coral blocks.")
                .define("spreadingCoral", true);
        RENEWABLE_SPONGES = builder
                .comment("Turn guardians struck by lightning into elder guardians.")
                .define("renewableSponges", true);
        PIGLINS_SPAWNING_IN_BASTIONS = builder
                .comment("Allow piglin brutes to naturally spawn inside bastion remnants.")
                .define("piglinsSpawningInBastions", true);

        builder.pop();
        SPEC = builder.build();
    }

    /** Block classes query this during vanilla static initialization. */
    public static boolean spreadingCoralEnabled() {
        return SPEC.isLoaded() && SPREADING_CORAL.get();
    }

    private AllVoidConfig() {
    }
}
