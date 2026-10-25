package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import dev.polaris_light.allvoid.helpers.CoralSpreader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.block.state.BlockBehaviour.class)
abstract class CalciteBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void allvoid$spreadCoral(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo callback) {
        if (AllVoidConfig.spreadingCoralEnabled() && state.is(Blocks.CALCITE)) {
            CoralSpreader.randomTick(state, level, pos, random);
            callback.cancel();
        }
    }
}
