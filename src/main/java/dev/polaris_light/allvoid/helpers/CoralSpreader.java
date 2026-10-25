package dev.polaris_light.allvoid.helpers;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;

public final class CoralSpreader {
    private static final double TARGET_TEMPERATURE = 0.65D;
    private static final double TARGET_CONTINENTALNESS = -0.3D;

    public static void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!AllVoidConfig.spreadingCoral || !AllVoidConfig.featuresEnabled(level)
                || level.dimension() != Level.OVERWORLD
                || !Blocks.TUBE_CORAL_BLOCK.defaultBlockState().canSurvive(level, pos)) {
            return;
        }

        List<Block> possible = possibleConversions(level, pos);
        if (possible.isEmpty()) {
            return;
        }

        double suitability = coralSuitability(level, pos);
        float chance = Mth.lerp((float) (suitability * suitability), 0.01F, 0.5F);
        if (random.nextFloat() >= chance) {
            return;
        }
        level.setBlockAndUpdate(pos, possible.get(random.nextInt(possible.size())).defaultBlockState());
    }

    private static List<Block> possibleConversions(ServerLevel level, BlockPos pos) {
        Map<Block, Integer> counts = new HashMap<>();
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            BlockState nearbyState = level.getBlockState(nearby);
            if (nearbyState.is(BlockTags.CORAL_BLOCKS)) {
                counts.merge(nearbyState.getBlock(), 1, Integer::sum);
            }
        }
        List<Block> possible = new ArrayList<>();
        counts.forEach((block, count) -> {
            if (count >= 8) {
                possible.add(block);
            }
        });
        return possible;
    }

    private static double coralSuitability(ServerLevel level, BlockPos pos) {
        ServerChunkCache chunkCache = level.getChunkSource();
        if (!(chunkCache.getGenerator() instanceof NoiseBasedChunkGenerator)) {
            return 0.5D;
        }
        try {
            RandomState randomState = chunkCache.randomState();
            NoiseRouter router = randomState.router();
            DensityFunction.SinglePointContext point =
                    new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
            double temperature = router.temperature().compute(point);
            double continentalness = router.continents().compute(point);
            return Mth.clamp(1.0D - (Mth.square(temperature - TARGET_TEMPERATURE)
                    + Mth.square(continentalness - TARGET_CONTINENTALNESS)), 0.0D, 1.0D);
        } catch (RuntimeException ignored) {
            return 0.5D;
        }
    }

    private CoralSpreader() {
    }
}
