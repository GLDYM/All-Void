package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.helpers.PaleBlossomCreakingHeart;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaplingBlock.class)
abstract class SaplingBlockMixin {
    @Inject(
            method = "advanceTree",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/grower/TreeGrower;"
                            + "growTree(Lnet/minecraft/server/level/ServerLevel;"
                            + "Lnet/minecraft/world/level/chunk/ChunkGenerator;"
                            + "Lnet/minecraft/core/BlockPos;"
                            + "Lnet/minecraft/world/level/block/state/BlockState;"
                            + "Lnet/minecraft/util/RandomSource;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void allvoid$paleBlossomCreakingHeart(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            RandomSource random,
            CallbackInfo callback
    ) {
        PaleBlossomCreakingHeart.afterTreeGrowth(level, pos, state, random);
    }
}
