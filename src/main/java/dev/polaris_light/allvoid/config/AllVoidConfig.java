package dev.polaris_light.allvoid.config;

import dev.polaris_light.allvoid.AllVoidChunkGenerator;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;

/** Common settings for the features added by All Void. */
public final class AllVoidConfig {
    public enum RenewableCoralMode {
        FALSE,
        TRUE,
        EXPANDED
    }

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ALLOW_FEATURES_IN_NON_VOID_WORLDS;
    public static final ModConfigSpec.BooleanValue RENEWABLE_NETHERRACK;
    public static final ModConfigSpec.BooleanValue TALL_FLOWERS_FROM_WANDERING_TRADER;
    public static final ModConfigSpec.BooleanValue LIGHTNING_ELECTRIFIES_VINES;
    public static final ModConfigSpec.BooleanValue GATEWAYS_SPAWN_CHORUS;
    public static final ModConfigSpec.BooleanValue HUGE_MUSHROOMS_SPREAD_MYCELIUM;
    public static final ModConfigSpec.BooleanValue SPREADING_SMALL_DRIPLEAVES;
    public static final ModConfigSpec.BooleanValue SPREADING_CORAL;
    public static final ModConfigSpec.EnumValue<RenewableCoralMode> RENEWABLE_CORAL;
    public static final ModConfigSpec.BooleanValue RENEWABLE_DEEPSLATE;
    public static final ModConfigSpec.BooleanValue RENEWABLE_SPONGES;
    public static final ModConfigSpec.BooleanValue PALE_BLOSSOM_CREAKING_HEART;
    public static final ModConfigSpec.BooleanValue PIGLINS_SPAWNING_IN_BASTIONS;

    public static volatile boolean allowFeaturesInNonVoidWorlds;
    public static volatile boolean renewableNetherrack = true;
    public static volatile boolean tallFlowersFromWanderingTrader = true;
    public static volatile boolean lightningElectrifiesVines = true;
    public static volatile boolean gatewaysSpawnChorus = true;
    public static volatile boolean hugeMushroomsSpreadMycelium = true;
    public static volatile boolean spreadingSmallDripleaves = true;
    public static volatile boolean spreadingCoral;
    public static volatile RenewableCoralMode renewableCoral = RenewableCoralMode.TRUE;
    public static volatile boolean renewableDeepslate = true;
    public static volatile boolean renewableSponges = true;
    public static volatile boolean paleBlossomCreakingHeart = true;
    public static volatile boolean piglinsSpawningInBastions = true;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("All Void feature settings").translation("allvoid.config.features").push("features");

        ALLOW_FEATURES_IN_NON_VOID_WORLDS = builder
                .comment("Allow All Void features to run in worlds that do not use the All Void generator.")
                .translation("allvoid.config.features.allow_features_in_non_void_worlds")
                .define("allowFeaturesInNonVoidWorlds", false);
        RENEWABLE_NETHERRACK = builder
                .comment("Generate netherrack or nylium beneath newly created Nether portals.")
                .translation("allvoid.config.features.renewable_netherrack")
                .define("renewableNetherrack", true);
        TALL_FLOWERS_FROM_WANDERING_TRADER = builder
                .comment("Add Bedrock-style tall flower trades to wandering traders.")
                .translation("allvoid.config.features.tall_flowers_from_wandering_trader")
                .define("tallFlowersFromWanderingTrader", true);
        LIGHTNING_ELECTRIFIES_VINES = builder
                .comment("Turn vines attached to glowstone into glow lichen when lightning strikes.")
                .translation("allvoid.config.features.lightning_electrifies_vines")
                .define("lightningElectrifiesVines", true);
        GATEWAYS_SPAWN_CHORUS = builder
                .comment("Grow a chorus plant on newly generated End Gateway islands.")
                .translation("allvoid.config.features.gateways_spawn_chorus")
                .define("gatewaysSpawnChorus", true);
        HUGE_MUSHROOMS_SPREAD_MYCELIUM = builder
                .comment("Spread mycelium around successfully generated huge mushrooms.")
                .translation("allvoid.config.features.huge_mushrooms_spread_mycelium")
                .define("hugeMushroomsSpreadMycelium", true);
        SPREADING_SMALL_DRIPLEAVES = builder
                .comment("Allow suitable small dripleaves to propagate on random ticks.")
                .translation("allvoid.config.features.spreading_small_dripleaves")
                .define("spreadingSmallDripleaves", true);
        SPREADING_CORAL = builder
                .comment("Allow calcite surrounded by coral to convert into coral blocks.")
                .translation("allvoid.config.features.spreading_coral")
                .define("spreadingCoral", true);
        RENEWABLE_CORAL = builder
                .comment("Grow coral structures from coral plants with bone meal.",
                        "EXPANDED also allows coral fans to be fertilized.")
                .translation("allvoid.config.features.renewable_coral")
                .defineEnum("renewableCoral", RenewableCoralMode.TRUE);
        RENEWABLE_DEEPSLATE = builder
                .comment("Generate deepslate and cobbled deepslate instead of stone below Y=0.")
                .translation("allvoid.config.features.renewable_deepslate")
                .define("renewableDeepslate", true);
        RENEWABLE_SPONGES = builder
                .comment("Turn guardians struck by lightning into elder guardians.")
                .translation("allvoid.config.features.renewable_sponges")
                .define("renewableSponges", true);
        PALE_BLOSSOM_CREAKING_HEART = builder
                .comment("Allow pale oak saplings grown near an open eyeblossom in a pale garden",
                        "to produce a creaking heart. Requires Vanilla Backport.")
                .translation("allvoid.config.features.pale_blossom_creaking_heart")
                .define("paleBlossomCreakingHeart", true);
        PIGLINS_SPAWNING_IN_BASTIONS = builder
                .comment("Allow piglin brutes to naturally spawn inside bastion remnants.")
                .translation("allvoid.config.features.piglins_spawning_in_bastions")
                .define("piglinsSpawningInBastions", true);

        builder.pop();
        SPEC = builder.build();
    }

    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            bake();
        }
    }

    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            bake();
        }
    }

    /** Copies the current config values used by gameplay code. */
    public static void bake() {
        allowFeaturesInNonVoidWorlds = ALLOW_FEATURES_IN_NON_VOID_WORLDS.get();
        renewableNetherrack = RENEWABLE_NETHERRACK.get();
        tallFlowersFromWanderingTrader = TALL_FLOWERS_FROM_WANDERING_TRADER.get();
        lightningElectrifiesVines = LIGHTNING_ELECTRIFIES_VINES.get();
        gatewaysSpawnChorus = GATEWAYS_SPAWN_CHORUS.get();
        hugeMushroomsSpreadMycelium = HUGE_MUSHROOMS_SPREAD_MYCELIUM.get();
        spreadingSmallDripleaves = SPREADING_SMALL_DRIPLEAVES.get();
        spreadingCoral = SPREADING_CORAL.get();
        renewableCoral = RENEWABLE_CORAL.get();
        renewableDeepslate = RENEWABLE_DEEPSLATE.get();
        renewableSponges = RENEWABLE_SPONGES.get();
        paleBlossomCreakingHeart = PALE_BLOSSOM_CREAKING_HEART.get();
        piglinsSpawningInBastions = PIGLINS_SPAWNING_IN_BASTIONS.get();
    }

    public static boolean featuresEnabled(ChunkGenerator generator) {
        return generator instanceof AllVoidChunkGenerator
                || allowFeaturesInNonVoidWorlds;
    }

    public static boolean featuresEnabled(ServerLevel level) {
        return featuresEnabled(level.getChunkSource().getGenerator());
    }

    public static boolean featuresEnabled(Level level) {
        return level instanceof ServerLevel serverLevel && featuresEnabled(serverLevel);
    }

    private AllVoidConfig() {
    }
}
