package dev.polaris_light.allvoid.mixin;

import dev.polaris_light.allvoid.AllVoid;
import dev.polaris_light.allvoid.worldgen.AllVoidDataPacks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Selects the All Void data pack together with the All Void world preset. */
@Mixin(WorldCreationUiState.class)
abstract class WorldCreationUiStateMixin {
    private static final ResourceKey<WorldPreset> ALL_VOID_PRESET = ResourceKey.create(
            net.minecraft.core.registries.Registries.WORLD_PRESET,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(AllVoid.MOD_ID, "all_void")
    );

    @Shadow
    protected abstract boolean tryUpdateDataConfiguration(WorldDataConfiguration configuration);

    @Inject(method = "setWorldType", at = @At("RETURN"))
    private void allvoid$selectDataPack(WorldCreationUiState.WorldTypeEntry worldType, CallbackInfo callback) {
        boolean allVoid = worldType.preset() != null
                && worldType.preset().unwrapKey().map(ALL_VOID_PRESET::equals).orElse(false);
        WorldCreationContext context = ((WorldCreationUiState) (Object) this).getSettings();
        WorldDataConfiguration current = context.dataConfiguration();
        DataPackConfig packs = current.dataPacks();
        List<String> enabled = new ArrayList<>(packs.getEnabled());
        List<String> disabled = new ArrayList<>(packs.getDisabled());
        boolean enabledBefore = enabled.contains(AllVoidDataPacks.ALL_VOID_PACK_ID);
        boolean disabledBefore = disabled.contains(AllVoidDataPacks.ALL_VOID_PACK_ID);
        DataPackConfig updated;
        if (allVoid) {
            if (enabledBefore && !disabledBefore) {
                return;
            }
            updated = AllVoidDataPacks.enableAllVoid(packs);
        } else {
            if (!enabledBefore) {
                return;
            }
            enabled.remove(AllVoidDataPacks.ALL_VOID_PACK_ID);
            if (!disabledBefore) {
                disabled.add(AllVoidDataPacks.ALL_VOID_PACK_ID);
            }
            updated = new DataPackConfig(enabled, disabled);
        }
        tryUpdateDataConfiguration(new WorldDataConfiguration(
                updated,
                current.enabledFeatures()
        ));
    }
}
