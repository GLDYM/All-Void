package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SinglePieceStructure.class)
abstract class SinglePieceStructureMixin {
    @Redirect(
            method = "findGenerationPoint",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/Structure;getLowestY(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;II)I")
    )
    private int allvoid$desertPyramidHeight(Structure.GenerationContext context, int width, int depth) {
        if ((Object) this instanceof DesertPyramidStructure
                && AllVoidConfig.featuresEnabled(context.chunkGenerator())) {
            // SinglePieceStructure rejects locations below sea level before it creates the piece.
            return context.chunkGenerator().getSeaLevel();
        }
        return StructureAccessor.allvoid$invokeGetLowestY(context, width, depth);
    }

    @Inject(method = "findGenerationPoint", at = @At("RETURN"), cancellable = true)
    private void allvoid$setDesertPyramidGenerationHeight(
            Structure.GenerationContext context,
            CallbackInfoReturnable<Optional<Structure.GenerationStub>> callback
    ) {
        if (!((Object) this instanceof DesertPyramidStructure)
                || !AllVoidConfig.featuresEnabled(context.chunkGenerator())) {
            return;
        }

        int y = Math.max(0, context.heightAccessor().getMinBuildHeight());
        callback.setReturnValue(callback.getReturnValue().map(stub -> {
            BlockPos position = stub.position();
            return new Structure.GenerationStub(
                    new BlockPos(position.getX(), y, position.getZ()),
                    stub.generator()
            );
        }));
    }
}
