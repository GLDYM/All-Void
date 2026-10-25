package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PortalForcer.class)
abstract class PortalForcerMixin {
    @Shadow
    @Final
    protected ServerLevel level;

    @Inject(method = "createPortal", at = @At("RETURN"))
    private void allvoid$addRenewableNetherBlocks(
            BlockPos pos,
            Direction.Axis axis,
            CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> callback
    ) {
        if (!AllVoidConfig.renewableNetherrack
                || !AllVoidConfig.featuresEnabled(level)
                || callback.getReturnValue().isEmpty()) {
            return;
        }

        BlockPos portal = callback.getReturnValue().get().minCorner;
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        Direction perpendicular = direction.getClockWise();
        for (int parallel = -1; parallel < 3; parallel++) {
            for (int side = -2; side < 3; side++) {
                if ((Math.abs(side) == 1 && (parallel == -1 || parallel == 2))
                        || (Math.abs(side) == 2 && (parallel == 0 || parallel == 1))) {
                    BlockPos skirt = portal.offset(
                            direction.getStepX() * parallel + perpendicular.getStepX() * side,
                            -1,
                            direction.getStepZ() * parallel + perpendicular.getStepZ() * side);
                    level.setBlockAndUpdate(skirt, portalSkirtBlock(skirt));
                }
            }
        }
    }

    private net.minecraft.world.level.block.state.BlockState portalSkirtBlock(BlockPos pos) {
        if (level.getBiome(pos).is(Biomes.CRIMSON_FOREST)) {
            return Blocks.CRIMSON_NYLIUM.defaultBlockState();
        }
        if (level.getBiome(pos).is(Biomes.WARPED_FOREST)) {
            return Blocks.WARPED_NYLIUM.defaultBlockState();
        }
        return Blocks.NETHERRACK.defaultBlockState();
    }
}
