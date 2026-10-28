package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.helpers.RenewableDeepslate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
abstract class LavaFluidMixin {
    @Inject(
            method = "spreadTo",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;"
                            + "defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            cancellable = true
    )
    private void allvoid$generateDeepslate(
            LevelAccessor world,
            BlockPos pos,
            BlockState targetState,
            Direction direction,
            FluidState fluidState,
            CallbackInfo callback
    ) {
        if (!(world instanceof Level level)
                || direction != Direction.DOWN
                || !(targetState.getBlock() instanceof LiquidBlock)) {
            return;
        }

        FluidState targetFluid = level.getFluidState(pos);
        if (targetFluid.is(FluidTags.WATER)
                && RenewableDeepslate.isEnabled(level, pos)) {
            RenewableDeepslate.placeDeepslate(level, pos);
            callback.cancel();
        }
    }
}
