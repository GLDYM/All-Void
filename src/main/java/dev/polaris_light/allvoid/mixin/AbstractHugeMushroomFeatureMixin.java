package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHugeMushroomFeature.class)
abstract class AbstractHugeMushroomFeatureMixin {
    @Inject(method = "place", at = @At("RETURN"))
    private void allvoid$spreadMycelium(
            FeaturePlaceContext<HugeMushroomFeatureConfiguration> context,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!AllVoidConfig.HUGE_MUSHROOMS_SPREAD_MYCELIUM.get() || !callback.getReturnValue()) {
            return;
        }
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos ground = origin.offset(x, -1, z);
                if (level.getBlockState(ground).is(BlockTags.DIRT)) {
                    level.setBlock(ground, Blocks.MYCELIUM.defaultBlockState(), 2);
                }
            }
        }
    }
}
