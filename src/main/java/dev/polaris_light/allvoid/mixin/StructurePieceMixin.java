package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructurePiece.class)
abstract class StructurePieceMixin {
    @Inject(method = "fillColumnDown", at = @At("HEAD"), cancellable = true)
    private void allvoid$skipTerrainSupport(WorldGenLevel level, BlockState state, int x, int y, int z,
                                             BoundingBox box, CallbackInfo callback) {
        if (AllVoidConfig.featuresEnabled(level.getLevel())) {
            callback.cancel();
        }
    }
}
