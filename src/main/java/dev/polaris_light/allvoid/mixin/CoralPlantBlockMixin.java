package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.helpers.RenewableCoralBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CoralPlantBlock.class)
abstract class CoralPlantBlockMixin implements RenewableCoralBlock {
}
