package dev.polaris_light.allvoid.helpers;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class LightningConverter {
    public static void strike(Level level, BlockPos strikePos) {
        if (!AllVoidConfig.LIGHTNING_ELECTRIFIES_VINES.get()) {
            return;
        }

        BlockState rawState = level.getBlockState(strikePos);
        BlockPos glowstonePos = strikePos;
        BlockState glowstone = rawState;
        if (rawState.getBlock() instanceof LightningRodBlock && rawState.hasProperty(LightningRodBlock.FACING)) {
            glowstonePos = strikePos.relative(rawState.getValue(LightningRodBlock.FACING).getOpposite());
            glowstone = level.getBlockState(glowstonePos);
        }
        if (!glowstone.is(Blocks.GLOWSTONE)) {
            return;
        }

        for (Direction direction : Direction.values()) {
            BlockPos vinePos = glowstonePos.relative(direction);
            BlockState vine = level.getBlockState(vinePos);
            Direction face = direction.getOpposite();
            if (vine.is(Blocks.VINE) && vine.hasProperty(VineBlock.getPropertyForFace(face))
                    && vine.getValue(VineBlock.getPropertyForFace(face))) {
                level.setBlockAndUpdate(vinePos,
                        Blocks.GLOW_LICHEN.defaultBlockState().setValue(GlowLichenBlock.getFaceProperty(face), true));
            }
        }
    }

    private LightningConverter() {
    }
}
