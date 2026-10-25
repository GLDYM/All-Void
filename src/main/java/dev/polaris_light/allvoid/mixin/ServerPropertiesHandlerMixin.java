package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.worldgen.AllVoidDataPacks;
import java.util.Properties;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.DataPackConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Enables the built-in data pack for dedicated servers configured with the All Void preset. */
@Mixin(DedicatedServerProperties.class)
abstract class ServerPropertiesHandlerMixin {
    @Shadow
    @Final
    @Mutable
    public DataPackConfig initialDataPackConfiguration;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void allvoid$enableDataPackForPreset(Properties properties, CallbackInfo callback) {
        if (AllVoidDataPacks.ALL_VOID_LEVEL_TYPE.equals(
                properties.getProperty("level-type", "minecraft:normal"))) {
            initialDataPackConfiguration = AllVoidDataPacks.enableAllVoid(initialDataPackConfiguration);
        }
    }
}
