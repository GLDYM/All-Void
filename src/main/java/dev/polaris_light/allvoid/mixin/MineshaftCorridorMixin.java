package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MineshaftPieces.MineShaftCorridor.class)
abstract class MineshaftCorridorMixin {
    @Inject(method = "createChest(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;IIILnet/minecraft/resources/ResourceKey;)Z", at = @At("HEAD"), cancellable = true)
    private void allvoid$createChestMinecartWithoutFloor(WorldGenLevel level, BoundingBox box,
                                                          RandomSource random, int x, int y, int z,
                                                          ResourceKey<LootTable> lootTable,
                                                          CallbackInfoReturnable<Boolean> callback) {
        if (!AllVoidConfig.featuresEnabled(level.getLevel().getChunkSource().getGenerator())) return;

        StructurePiece piece = (StructurePiece) (Object) this;
        BoundingBox pieceBox = piece.getBoundingBox();
        BlockPos.MutableBlockPos pos = worldPos(piece, pieceBox, x, y, z);
        if (!box.isInside(pos) || !level.getBlockState(pos).isAir()) return;

        level.setBlock(pos, Blocks.RAIL.defaultBlockState(), 2);
        MinecartChest minecart = new MinecartChest(level.getLevel(), pos.getX() + 0.5,
                pos.getY() + 0.5, pos.getZ() + 0.5);
        minecart.setLootTable(lootTable, random.nextLong());
        level.addFreshEntity(minecart);
        callback.setReturnValue(true);
    }

    private static BlockPos.MutableBlockPos worldPos(StructurePiece piece, BoundingBox box, int x, int y, int z) {
        var direction = piece.getOrientation();
        int worldX;
        int worldZ;
        if (direction == null) {
            worldX = x;
            worldZ = z;
        } else {
            switch (direction) {
                case SOUTH -> { worldX = box.minX() + x; worldZ = box.maxZ() - z; }
                case NORTH -> { worldX = box.minX() + x; worldZ = box.minZ() + z; }
                case WEST -> { worldX = box.maxX() - z; worldZ = box.minZ() + x; }
                case EAST -> { worldX = box.minX() + z; worldZ = box.minZ() + x; }
                default -> { worldX = x; worldZ = z; }
            }
        }
        return new BlockPos.MutableBlockPos(worldX, box.minY() + y, worldZ);
    }
}
