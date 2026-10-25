package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.worldgen.AllVoidDataPacks;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Makes the built-in All Void data available while the world creation registries load. */
@Mixin(net.minecraft.client.gui.screens.worldselection.CreateWorldScreen.class)
abstract class CreateWorldScreenMixin {
    @ModifyArg(
            method = "openFresh",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/worldselection/CreateWorldScreen;"
                            + "createDefaultLoadConfig(Lnet/minecraft/server/packs/repository/PackRepository;"
                            + "Lnet/minecraft/world/level/WorldDataConfiguration;)"
                            + "Lnet/minecraft/server/WorldLoader$InitConfig;"
            ),
            index = 1
    )
    private static WorldDataConfiguration allvoid$enableDataPack(
            WorldDataConfiguration configuration
    ) {
        return AllVoidDataPacks.enableAllVoid(configuration);
    }
}
