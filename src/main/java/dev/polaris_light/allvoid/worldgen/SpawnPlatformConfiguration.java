package dev.polaris_light.allvoid.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SpawnPlatformConfiguration(Platform platform, boolean spawnRelative) implements FeatureConfiguration {
    public static final Codec<SpawnPlatformConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Platform.CODEC.fieldOf("platform").forGetter(SpawnPlatformConfiguration::platform),
            Codec.BOOL.optionalFieldOf("spawn_relative", true).forGetter(SpawnPlatformConfiguration::spawnRelative)
    ).apply(instance, SpawnPlatformConfiguration::new));

    public record Platform(ResourceLocation structure, BlockPos pos) {
        public static final Codec<Platform> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("structure").forGetter(Platform::structure),
                BlockPos.CODEC.fieldOf("pos").forGetter(Platform::pos)
        ).apply(instance, Platform::new));
    }
}
