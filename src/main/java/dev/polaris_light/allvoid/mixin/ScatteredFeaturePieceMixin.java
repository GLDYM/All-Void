package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.AllVoidChunkGenerator;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScatteredFeaturePiece.class)
abstract class ScatteredFeaturePieceMixin {
    @Shadow
    protected int heightPosition;

    @Inject(method = "updateHeightPositionToLowestGroundHeight", at = @At("HEAD"), cancellable = true)
    private void allvoid$keepDesertPyramidAtFixedHeight(
            LevelAccessor level,
            int offset,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!((Object) this instanceof DesertPyramidPiece)
                || !(level instanceof WorldGenLevel worldGenLevel)
                || !(worldGenLevel.getLevel().getChunkSource().getGenerator() instanceof AllVoidChunkGenerator)
                || this.heightPosition >= 0) {
            return;
        }

        StructurePiece piece = (StructurePiece) (Object) this;
        int targetY = Math.max(0, worldGenLevel.getMinBuildHeight());
        piece.move(0, targetY - piece.getBoundingBox().minY(), 0);
        this.heightPosition = targetY;
        callback.setReturnValue(true);
    }
}
