package dev.polaris_light.allvoid;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import java.util.concurrent.CompletableFuture;

/**
 * A noise generator which keeps the original biome source and settings, but
 * omits terrain filling. Structures and biome decoration still use the normal
 * generator pipeline. Empty heightmaps are used in the Overworld and Nether
 * so ordinary surface decorations cannot turn the void into a layer of trees
 * or ores. The End keeps its original noise heights because return gateways
 * use {@code WORLD_SURFACE_WG}; its platform and spikes are fixed/end features.
 */
public final class AllVoidChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<AllVoidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(AllVoidChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(AllVoidChunkGenerator::generatorSettings)
    ).apply(instance, AllVoidChunkGenerator::new));

    public AllVoidChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    /** Wraps a vanilla noise generator used by a datapack or mod-added dimension. */
    public static net.minecraft.world.level.chunk.ChunkGenerator wrap(
            net.minecraft.world.level.chunk.ChunkGenerator generator) {
        if (generator instanceof AllVoidChunkGenerator || !(generator instanceof NoiseBasedChunkGenerator noise)) {
            return generator;
        }
        return new AllVoidChunkGenerator(noise.getBiomeSource(), noise.generatorSettings());
    }

    @Override
    protected MapCodec<? extends net.minecraft.world.level.chunk.ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState,
                                                          StructureManager structureManager, ChunkAccess chunk) {
        populateVoidHeightmaps(randomState, chunk);
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager,
                             RandomState randomState, ChunkAccess chunk) {
        // Terrain is intentionally empty. Structures and features are applied later.
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState,
                             BiomeManager biomeManager, StructureManager structureManager,
                             ChunkAccess chunk, GenerationStep.Carving carving) {
        // Carvers cannot add terrain to an empty chunk and are skipped deliberately.
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        super.applyBiomeDecoration(level, chunk, structureManager);
        addSuspiciousBlockSupports(level, chunk);
    }

    private void populateVoidHeightmaps(RandomState randomState, ChunkAccess chunk) {
        int bits = Mth.ceillog2(chunk.getHeight() + 1);
        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight();
        ChunkPos pos = chunk.getPos();
        for (Heightmap.Types type : Heightmap.Types.values()) {
            SimpleBitStorage storage = new SimpleBitStorage(bits, 256);
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localX = 0; localX < 16; localX++) {
                    int x = pos.getMinBlockX() + localX;
                    int z = pos.getMinBlockZ() + localZ;
                    int height = generatorSettings().is(NoiseGeneratorSettings.END)
                            ? super.getBaseHeight(x, z, type, chunk.getHeightAccessorForGeneration(), randomState)
                            : minY + 5;
                    height = Mth.clamp(height, minY, maxY);
                    storage.set(localX | (localZ << 4), height - minY);
                }
            }
            chunk.setHeightmap(type, storage.getRaw());
        }
    }

    private void addSuspiciousBlockSupports(WorldGenLevel level, ChunkAccess chunk) {
        ChunkPos pos = chunk.getPos();
        for (int y = chunk.getMinBuildHeight(); y < chunk.getMaxBuildHeight(); y++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localX = 0; localX < 16; localX++) {
                    var suspiciousPos = new net.minecraft.core.BlockPos(pos.getMinBlockX() + localX, y, pos.getMinBlockZ() + localZ);
                    Block block = level.getBlockState(suspiciousPos).getBlock();
                    if (block != Blocks.SUSPICIOUS_SAND && block != Blocks.SUSPICIOUS_GRAVEL) continue;

                    var supportPos = suspiciousPos.below();
                    while (supportPos.getY() >= chunk.getMinBuildHeight()) {
                        var supportState = level.getBlockState(supportPos);
                        if (supportState.isAir()) {
                            level.setBlock(supportPos, Blocks.SANDSTONE.defaultBlockState(), 2);
                            break;
                        }
                        if (!(supportState.getBlock() instanceof Fallable)) {
                            break;
                        }
                        supportPos = supportPos.below();
                    }
                }
            }
        }
    }

}
