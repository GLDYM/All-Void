package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity.class)
abstract class TheEndGatewayBlockEntityMixin {
    @Redirect(
            method = "findOrCreateValidTeleportPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z")
    )
    private static boolean allvoid$generateChorusOnGatewayIsland(
            ConfiguredFeature<?, ?> feature,
            WorldGenLevel level,
            ChunkGenerator generator,
            RandomSource random,
            BlockPos origin
    ) {
        boolean placed = feature.place(level, generator, random, origin);
        if (placed && AllVoidConfig.GATEWAYS_SPAWN_CHORUS.get()) {
            placeChorus(level, origin, random);
        }
        return placed;
    }

    private static void placeChorus(WorldGenLevel level, BlockPos origin, RandomSource random) {
        for (int attempt = 0; attempt < 32; attempt++) {
            BlockPos candidate = origin.offset(random.nextInt(11) - 5, random.nextInt(7) - 3, random.nextInt(11) - 5);
            if (!level.getBlockState(candidate).is(Blocks.END_STONE)
                    || !level.isEmptyBlock(candidate.above())) {
                continue;
            }
            boolean clear = true;
            for (var direction : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                if (!level.isEmptyBlock(candidate.above().relative(direction))) {
                    clear = false;
                    break;
                }
            }
            if (clear) {
                ChorusFlowerBlock.generatePlant(level, candidate.above(), random, 8);
                return;
            }
        }
    }
}
