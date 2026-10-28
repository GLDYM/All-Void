package dev.polaris_light.allvoid.helpers;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface RenewableCoralBlock extends BonemealableBlock {
    default boolean allvoid$isExpandedOnly() {
        return false;
    }

    default boolean allvoid$isRenewableCoralEnabled(LevelReader world) {
        AllVoidConfig.RenewableCoralMode mode = AllVoidConfig.renewableCoral;
        if (mode == AllVoidConfig.RenewableCoralMode.FALSE) {
            return false;
        }
        if (mode == AllVoidConfig.RenewableCoralMode.TRUE && allvoid$isExpandedOnly()) {
            return false;
        }
        return !(world instanceof ServerLevel level)
                || AllVoidConfig.featuresEnabled(level);
    }

    @Override
    default boolean isValidBonemealTarget(
            LevelReader world,
            BlockPos pos,
            BlockState state
    ) {
        return allvoid$isRenewableCoralEnabled(world)
                && state.getValue(BaseCoralPlantTypeBlock.WATERLOGGED)
                && world.getFluidState(pos.above()).is(FluidTags.WATER);
    }

    @Override
    default boolean isBonemealSuccess(
            Level world,
            RandomSource random,
            BlockPos pos,
            BlockState state
    ) {
        return allvoid$isRenewableCoralEnabled(world) && random.nextFloat() < 0.15F;
    }

    @Override
    default void performBonemeal(
            ServerLevel level,
            RandomSource random,
            BlockPos pos,
            BlockState state
    ) {
        if (allvoid$isRenewableCoralEnabled(level)) {
            RenewableCoral.grow(level, random, pos, state);
        }
    }
}
