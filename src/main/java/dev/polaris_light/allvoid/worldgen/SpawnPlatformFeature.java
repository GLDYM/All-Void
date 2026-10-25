package dev.polaris_light.allvoid.worldgen;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Places the configurable structure used for the world spawn platform. */
public final class SpawnPlatformFeature extends Feature<SpawnPlatformConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger("allvoid");

    public SpawnPlatformFeature() {
        super(SpawnPlatformConfiguration.CODEC);
    }

    public static BlockPos placementOrigin(SpawnPlatformConfiguration config, BlockPos spawn) {
        BlockPos base = config.spawnRelative() ? spawn.atY(0) : BlockPos.ZERO;
        return base.offset(config.platform().pos());
    }

    public static BlockPos spawnPosition(SpawnPlatformConfiguration config, BlockPos spawn) {
        BlockPos base = config.spawnRelative() ? spawn.atY(0) : BlockPos.ZERO;
        return base.offset(0, config.platform().pos().getY() + 1, 0);
    }

    /** Checks the configured template footprint instead of assuming a particular platform block. */
    public static boolean isStructurePresent(LevelReader level, StructureTemplate template, BlockPos origin) {
        Vec3i size = template.getSize();
        for (int y = 0; y < size.getY(); y++) {
            for (int z = 0; z < size.getZ(); z++) {
                for (int x = 0; x < size.getX(); x++) {
                    if (!level.getBlockState(origin.offset(x, y, z)).isAir()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean place(FeaturePlaceContext<SpawnPlatformConfiguration> context) {
        if (!AllVoidConfig.featuresEnabled(context.chunkGenerator())) {
            return false;
        }
        SpawnPlatformConfiguration config = context.config();
        SpawnPlatformConfiguration.Platform platform = config.platform();
        StructureTemplate template = context.level().getLevel().getStructureManager().get(platform.structure()).orElse(null);
        if (template == null) {
            LOGGER.warn("Missing spawn platform structure {}", platform.structure());
            return false;
        }

        boolean placed = template.placeInWorld(
                context.level(),
                placementOrigin(config, context.origin()),
                null,
                new StructurePlaceSettings(),
                context.random(),
                Block.UPDATE_CLIENTS
        );
        LOGGER.debug("Spawn platform template {} at {} (size {}) placed={}",
                platform.structure(), placementOrigin(config, context.origin()), template.getSize(), placed);
        return placed;
    }
}
