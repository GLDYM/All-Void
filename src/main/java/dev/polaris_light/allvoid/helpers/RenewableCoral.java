package dev.polaris_light.allvoid.helpers;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralClawFeature;
import net.minecraft.world.level.levelgen.feature.CoralTreeFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class RenewableCoral {
    public static void grow(
            ServerLevel level,
            RandomSource random,
            BlockPos pos,
            BlockState originalState
    ) {
        Block coralBlock = coralBlockFor(level, pos, originalState).orElse(null);
        if (coralBlock == null) {
            return;
        }

        BlockState coralState = coralBlock.defaultBlockState();
        level.setBlock(pos, Blocks.WATER.defaultBlockState(), Block.UPDATE_NONE);
        boolean grew = switch (random.nextInt(3)) {
            case 0 -> place(level, random, pos, new FixedCoralTreeFeature(coralState));
            case 1 -> place(level, random, pos, new FixedCoralClawFeature(coralState));
            default -> growCoralPatch(level, random, pos, coralState);
        };
        if (!grew) {
            level.setBlock(pos, originalState, Block.UPDATE_ALL);
            return;
        }

        if (random.nextInt(10) == 0) {
            BlockPos spongePos = pos.offset(
                    random.nextInt(16) - 8,
                    random.nextInt(8),
                    random.nextInt(16) - 8
            );
            if (level.getBlockState(spongePos).is(BlockTags.CORAL_BLOCKS)) {
                level.setBlock(spongePos, Blocks.WET_SPONGE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private static boolean growCoralPatch(
            ServerLevel level,
            RandomSource random,
            BlockPos pos,
            BlockState coralState
    ) {
        int sizeX = random.nextIntBetweenInclusive(3, 5);
        int sizeZ = random.nextIntBetweenInclusive(3, 5);
        int down = random.nextIntBetweenInclusive(1, 3);
        boolean grew = false;
        FixedCoralTreeFeature tree = new FixedCoralTreeFeature(coralState);

        for (int x = -sizeX; x <= sizeX; x++) {
            for (int z = -sizeZ; z <= sizeZ; z++) {
                if (random.nextFloat() < 0.1F) {
                    continue;
                }
                BlockPos target = pos.offset(x, -down, z);
                if (level.getBlockState(target).is(Blocks.WATER)
                        && level.getBlockState(target.above()).is(Blocks.WATER)) {
                    grew |= place(level, random, target, tree);
                }
            }
        }
        return grew;
    }

    private static boolean place(
            ServerLevel level,
            RandomSource random,
            BlockPos pos,
            CoralFeatureWithState feature
    ) {
        FeaturePlaceContext<NoneFeatureConfiguration> context =
                new FeaturePlaceContext<>(
                        Optional.empty(),
                        level,
                        level.getChunkSource().getGenerator(),
                        random,
                        pos,
                        NoneFeatureConfiguration.INSTANCE
                );
        return feature.place(context);
    }

    private static Optional<Block> coralBlockFor(
            ServerLevel level,
            BlockPos pos,
            BlockState state
    ) {
        var coralBlocks = level.registryAccess()
                .registryOrThrow(Registries.BLOCK)
                .getTag(BlockTags.CORAL_BLOCKS)
                .orElse(null);
        if (coralBlocks == null) {
            return Optional.empty();
        }

        var color = state.getMapColor(level, pos);
        List<Block> blocks = coralBlocks.stream().map(Holder::value).toList();
        return blocks.stream()
                .filter(block -> block.defaultBlockState().getMapColor(level, pos) == color)
                .findFirst()
                .or(() -> blocks.stream().findFirst());
    }

    private interface CoralFeatureWithState {
        boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context);
    }

    private static final class FixedCoralTreeFeature
            extends CoralTreeFeature implements CoralFeatureWithState {
        private final BlockState coralState;

        private FixedCoralTreeFeature(BlockState coralState) {
            super(NoneFeatureConfiguration.CODEC);
            this.coralState = coralState;
        }

        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            return placeFeature(
                    context.level(),
                    context.random(),
                    context.origin(),
                    coralState
            );
        }
    }

    private static final class FixedCoralClawFeature
            extends CoralClawFeature implements CoralFeatureWithState {
        private final BlockState coralState;

        private FixedCoralClawFeature(BlockState coralState) {
            super(NoneFeatureConfiguration.CODEC);
            this.coralState = coralState;
        }

        @Override
        public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
            return placeFeature(
                    context.level(),
                    context.random(),
                    context.origin(),
                    coralState
            );
        }
    }

    private RenewableCoral() {
    }
}
