package dev.polaris_light.allvoid;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(AllVoid.MOD_ID)
public final class AllVoid {
    public static final String MOD_ID = "allvoid";

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MOD_ID);
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<AllVoidChunkGenerator>> ALL_VOID_GENERATOR =
            CHUNK_GENERATORS.register("all_void", () -> AllVoidChunkGenerator.CODEC);

    public AllVoid(IEventBus modBus) {
        CHUNK_GENERATORS.register(modBus);
    }
}
