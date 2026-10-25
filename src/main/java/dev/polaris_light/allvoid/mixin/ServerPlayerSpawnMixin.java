package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Keeps All Void players on the generated platform instead of applying spawn-radius randomization. */
@Mixin(ServerPlayer.class)
abstract class ServerPlayerSpawnMixin {
    @Inject(method = "adjustSpawnLocation", at = @At("HEAD"), cancellable = true)
    private void allvoid$usePlatformSpawn(
            ServerLevel level,
            BlockPos spawn,
            CallbackInfoReturnable<BlockPos> callback
    ) {
        if (AllVoidConfig.featuresEnabled(level)) {
            callback.setReturnValue(spawn);
        }
    }
}
