package dev.polaris_light.allvoid;

import com.mojang.serialization.MapCodec;
import dev.polaris_light.allvoid.config.AllVoidConfig;
import dev.polaris_light.allvoid.worldgen.SpawnPlatformFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(AllVoid.MOD_ID)
public final class AllVoid {
    public static final String MOD_ID = "allvoid";

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MOD_ID);
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<AllVoidChunkGenerator>> ALL_VOID_GENERATOR =
            CHUNK_GENERATORS.register("all_void", () -> AllVoidChunkGenerator.CODEC);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MOD_ID);
    public static final DeferredHolder<Feature<?>, SpawnPlatformFeature> SPAWN_PLATFORM =
            FEATURES.register("spawn_platform", SpawnPlatformFeature::new);

    public AllVoid(IEventBus modBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, AllVoidConfig.SPEC);
        modBus.addListener(AllVoidConfig::onConfigLoading);
        modBus.addListener(AllVoidConfig::onConfigReloading);
        modBus.addListener(AllVoid::addOptionalDataPack);
        modBus.addListener(AllVoidEvents::registerSpawnPlacements);

        CHUNK_GENERATORS.register(modBus);
        FEATURES.register(modBus);
        AllVoidEvents events = new AllVoidEvents();
        NeoForge.EVENT_BUS.register(events);
    }

    private static void addOptionalDataPack(AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "all_void"),
                PackType.SERVER_DATA,
                Component.translatable("pack.allvoid.all_void.name"),
                PackSource.FEATURE,
                false,
                Pack.Position.TOP
        );
    }
}
