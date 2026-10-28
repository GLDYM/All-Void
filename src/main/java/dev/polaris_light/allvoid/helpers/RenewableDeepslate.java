package dev.polaris_light.allvoid.helpers;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

public final class RenewableDeepslate {
    public static boolean isEnabled(Level level, BlockPos pos) {
        return AllVoidConfig.renewableDeepslate
                && pos.getY() < 0
                && level.dimension() == Level.OVERWORLD
                && AllVoidConfig.featuresEnabled(level);
    }

    public static void placeCobbledDeepslate(Level level, BlockPos pos) {
        place(level, pos, Blocks.COBBLED_DEEPSLATE.defaultBlockState());
    }

    public static void placeDeepslate(Level level, BlockPos pos) {
        place(level, pos, Blocks.DEEPSLATE.defaultBlockState());
    }

    private static void place(Level level, BlockPos pos, BlockState defaultState) {
        BlockState eventState = EventHooks.fireFluidPlaceBlockEvent(level, pos, pos, defaultState);
        level.setBlock(pos, eventState, Block.UPDATE_ALL);
        level.levelEvent(1501, pos, 0);
    }

    private RenewableDeepslate() {
    }
}
