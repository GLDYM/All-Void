package dev.polaris_light.allvoid.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
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

    @Override
    public boolean place(FeaturePlaceContext<SpawnPlatformConfiguration> context) {
        SpawnPlatformConfiguration config = context.config();
        SpawnPlatformConfiguration.Platform platform = config.platform();
        StructureTemplate template = context.level().getLevel().getStructureManager().get(platform.structure()).orElse(null);
        if (template == null) {
            LOGGER.warn("Missing spawn platform structure {}", platform.structure());
            return false;
        }

        BlockPos base = config.spawnRelative() ? context.origin().atY(0) : BlockPos.ZERO;
        boolean placed = template.placeInWorld(
                context.level(),
                base.offset(platform.pos()),
                null,
                new StructurePlaceSettings(),
                context.random(),
                Block.UPDATE_CLIENTS
        );
        LOGGER.debug("Spawn platform template {} at {} (size {}) placed={}",
                platform.structure(), base.offset(platform.pos()), template.getSize(), placed);
        return placed;
    }
}
