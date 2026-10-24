package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.AllVoidChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

/** Applies the All Void generator to mod/datapack dimensions present in the level-stem registry. */
@Mixin(WorldDimensions.class)
public abstract class WorldDimensionsMixin {
    @Redirect(method = "lambda$bake$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;getOptional(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;"))
    private Optional<LevelStem> allvoid$wrapAdditionalDimensions(
            Registry<LevelStem> registry, ResourceKey<LevelStem> key) {
        Optional<LevelStem> original = registry.getOptional(key);
        if (original.isEmpty()) return original;
        WorldDimensions dimensions = (WorldDimensions) (Object) this;
        boolean allVoid = dimensions.dimensions().values().stream()
                .anyMatch(stem -> stem.generator() instanceof AllVoidChunkGenerator);
        if (!allVoid) return original;
        LevelStem stem = original.get();
        return Optional.of(new LevelStem(stem.type(), AllVoidChunkGenerator.wrap(stem.generator())));
    }
}
