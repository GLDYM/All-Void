package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.helpers.RenewableCoralBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CoralFanBlock.class)
abstract class CoralFanBlockMixin implements RenewableCoralBlock {
    @Override
    public boolean allvoid$isExpandedOnly() {
        return true;
    }
}
