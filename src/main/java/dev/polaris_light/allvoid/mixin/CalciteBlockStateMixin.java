package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
abstract class CalciteBlockStateMixin {
    @Inject(method = "isRandomlyTicking", at = @At("RETURN"), cancellable = true)
    private void allvoid$calciteRandomTicks(CallbackInfoReturnable<Boolean> callback) {
        if (AllVoidConfig.spreadingCoralEnabled()
                && ((BlockState) (Object) this).is(Blocks.CALCITE)) {
            callback.setReturnValue(true);
        }
    }
}
