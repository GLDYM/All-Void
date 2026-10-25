package dev.polaris_light.allvoid.helpers;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public final class SmallDripleafSpreader {
    private static final int REQUIRED_LIGHT = 5;
    private static final int MAX_DENSITY = 7;
    private static final float SPREAD_CHANCE = 0.15F;

    public static boolean isSpreadableState(BlockState state) {
        return state.is(Blocks.SMALL_DRIPLEAF)
                && state.getValue(SmallDripleafBlock.HALF) == DoubleBlockHalf.LOWER
                && state.getValue(BlockStateProperties.WATERLOGGED);
    }

    private static boolean canSpreadFrom(BlockState state, ServerLevel level, BlockPos pos) {
        if (!isSpreadableState(state)) {
            return false;
        }
        BlockPos top = pos.above();
        BlockState topState = level.getBlockState(top);
        return topState.is(Blocks.SMALL_DRIPLEAF)
                && topState.getValue(SmallDripleafBlock.HALF) == DoubleBlockHalf.UPPER
                && !topState.getValue(BlockStateProperties.WATERLOGGED)
                && level.getBlockState(pos.below()).is(Blocks.CLAY)
                && level.getMaxLocalRawBrightness(top) >= REQUIRED_LIGHT;
    }

    private static boolean canSpreadTo(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).is(Blocks.WATER)) {
            return false;
        }
        BlockPos top = pos.above();
        return !level.isOutsideBuildHeight(top)
                && level.isEmptyBlock(top)
                && level.getBlockState(pos.below()).is(Blocks.CLAY)
                && level.getMaxLocalRawBrightness(top) == REQUIRED_LIGHT
                && density(level, pos) <= MAX_DENSITY;
    }

    private static int density(ServerLevel level, BlockPos pos) {
        int count = 0;
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-2, 0, -2), pos.offset(2, 1, 2))) {
            if (level.getBlockState(nearby).is(Blocks.SMALL_DRIPLEAF)) {
                count++;
            }
        }
        return count;
    }

    private static int binomialOffset(int range, RandomSource random) {
        int offset = -range;
        for (int i = 0; i < range * 2; i++) {
            if (random.nextBoolean()) {
                offset++;
            }
        }
        return offset;
    }

    private static void placeNewDripleaf(ServerLevel level, BlockPos pos, RandomSource random) {
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState common = Blocks.SMALL_DRIPLEAF.defaultBlockState().setValue(SmallDripleafBlock.FACING, facing);
        level.setBlockAndUpdate(pos, common.setValue(BlockStateProperties.WATERLOGGED, true));
        level.setBlockAndUpdate(pos.above(), common.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));
    }

    public static void trySpread(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!AllVoidConfig.spreadingSmallDripleaves
                || !AllVoidConfig.featuresEnabled(level)
                || random.nextFloat() >= SPREAD_CHANCE
                || !canSpreadFrom(state, level, pos)) {
            return;
        }
        for (int attempt = 0; attempt < 3; attempt++) {
            BlockPos target = pos.offset(binomialOffset(5, random), binomialOffset(2, random), binomialOffset(5, random));
            if (canSpreadTo(level, target)) {
                placeNewDripleaf(level, target, random);
                return;
            }
        }
    }

    private SmallDripleafSpreader() {
    }
}
