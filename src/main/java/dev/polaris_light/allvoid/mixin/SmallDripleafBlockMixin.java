package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.helpers.SmallDripleafSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SmallDripleafBlock.class)
abstract class SmallDripleafBlockMixin extends DoublePlantBlock {
    protected SmallDripleafBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return SmallDripleafSpreader.isSpreadableState(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        SmallDripleafSpreader.trySpread(state, level, pos, random);
    }
}
