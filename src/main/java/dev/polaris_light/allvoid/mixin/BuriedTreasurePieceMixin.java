package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasurePieces;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuriedTreasurePieces.BuriedTreasurePiece.class)
abstract class BuriedTreasurePieceMixin {
    @Inject(method = "postProcess(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true)
    private void allvoid$placeExposedTreasure(WorldGenLevel level, StructureManager structureManager,
                                               ChunkGenerator generator, RandomSource random,
                                               BoundingBox writableArea, ChunkPos chunkPos,
                                               BlockPos pivot, CallbackInfo callback) {
        if (!AllVoidConfig.featuresEnabled(generator)) return;

        BlockPos treasurePos = new BlockPos(chunkPos.getMiddleBlockX(), level.getMinBuildHeight() + 1, chunkPos.getMiddleBlockZ());
        if (!writableArea.isInside(treasurePos)) return;
        if (!level.getBlockState(treasurePos).isAir()) {
            callback.cancel();
            return;
        }

        level.setBlock(treasurePos, Blocks.CHEST.defaultBlockState(), 2);
        if (level.getBlockEntity(treasurePos) instanceof RandomizableContainerBlockEntity container) {
            container.setLootTable(BuiltInLootTables.BURIED_TREASURE, random.nextLong());
        }
        callback.cancel();
    }
}
