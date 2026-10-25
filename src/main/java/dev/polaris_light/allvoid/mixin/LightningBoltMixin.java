package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.helpers.LightningConverter;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
abstract class LightningBoltMixin {
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;powerLightningRod()V")
    )
    private void allvoid$electrifyVines(CallbackInfo callback) {
        LightningBolt bolt = (LightningBolt) (Object) this;
        LightningConverter.strike(bolt.level(), bolt.blockPosition());
    }
}
