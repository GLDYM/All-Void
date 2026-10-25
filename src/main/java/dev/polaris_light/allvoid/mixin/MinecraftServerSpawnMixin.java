package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.AllVoid;
import dev.polaris_light.allvoid.AllVoidChunkGenerator;
import dev.polaris_light.allvoid.worldgen.SpawnPlatformConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Generates the spawn platform after vanilla has selected the actual spawn position. */
@Mixin(MinecraftServer.class)
abstract class MinecraftServerSpawnMixin {
    private static final Logger ALLVOID_LOGGER = LoggerFactory.getLogger("allvoid");
    private static final ResourceKey<ConfiguredFeature<?, ?>> SPAWN_PLATFORM = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(AllVoid.MOD_ID, "spawn_platform")
    );

    @Inject(method = "setInitialSpawn", at = @At("RETURN"))
    private static void allvoid$generateSpawnPlatform(
            ServerLevel level,
            ServerLevelData levelData,
            boolean generateBonusChest,
            boolean debugWorld,
            CallbackInfo callback
    ) {
        allvoid$ensureSpawnPlatform(level, levelData);
    }

    @Inject(method = "loadLevel", at = @At("RETURN"))
    private void allvoid$repairExistingSpawnPlatform(CallbackInfo callback) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        ServerLevel level = server.overworld();
        if (level != null) {
            allvoid$ensureSpawnPlatform(level, (ServerLevelData) level.getLevelData());
        }
    }

    private static void allvoid$ensureSpawnPlatform(ServerLevel level, ServerLevelData levelData) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        ALLVOID_LOGGER.debug("Initial spawn hook reached at {} with generator {}", levelData.getSpawnPos(), generator.getClass().getName());
        if (!(generator instanceof AllVoidChunkGenerator)) {
            return;
        }

        BlockPos spawn = levelData.getSpawnPos();
        Holder<ConfiguredFeature<?, ?>> feature = level.registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .get(SPAWN_PLATFORM)
                .orElse(null);
        if (feature == null) {
            ALLVOID_LOGGER.warn("Configured feature allvoid:spawn_platform was not found");
            return;
        }

        if (!(feature.value().config() instanceof SpawnPlatformConfiguration config)) {
            ALLVOID_LOGGER.warn("Configured feature allvoid:spawn_platform has an unexpected configuration");
            return;
        }

        BlockPos platformOrigin = spawn.atY(0).offset(config.platform().pos());
        BlockPos platformSpawn = platformOrigin.offset(
                -config.platform().pos().getX(),
                1,
                -config.platform().pos().getZ()
        );
        boolean alreadyPlaced = level.getBlockState(platformOrigin).is(Blocks.GRASS_BLOCK);
        boolean placed = alreadyPlaced;
        if (!alreadyPlaced) {
            RandomSource random = RandomSource.create(level.getSeed() ^ spawn.asLong());
            placed = feature.value().place(level, generator, random, spawn);
        }
        if (!placed) {
            ALLVOID_LOGGER.warn("Could not place All Void spawn platform at {}", spawn);
        } else {
            level.setDefaultSpawnPos(platformSpawn, 0.0F);
            ALLVOID_LOGGER.info("Placed All Void spawn platform at {} and reset world spawn", platformSpawn);
        }
    }
}
