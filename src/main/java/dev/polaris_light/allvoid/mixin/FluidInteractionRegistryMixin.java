package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.helpers.RenewableDeepslate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidInteractionRegistry.class)
abstract class FluidInteractionRegistryMixin {
    @Inject(method = "canInteract", at = @At("HEAD"), cancellable = true)
    private static void allvoid$generateCobbledDeepslate(
            Level level,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> callback
    ) {
        FluidState lava = level.getFluidState(pos);
        if (!RenewableDeepslate.isEnabled(level, pos)
                || !lava.is(FluidTags.LAVA)
                || lava.isSource()) {
            return;
        }

        for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            BlockPos adjacent = pos.relative(direction.getOpposite());
            if (level.getFluidState(adjacent).is(FluidTags.WATER)) {
                RenewableDeepslate.placeCobbledDeepslate(level, pos);
                callback.setReturnValue(true);
                return;
            }
        }
    }
}
