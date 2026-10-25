package dev.polaris_light.allvoid.worldgen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;

/** Shared data-pack configuration used by the client world screen and dedicated servers. */
public final class AllVoidDataPacks {
    public static final String ALL_VOID_PACK_ID = "mod/allvoid:all_void";
    public static final String ALL_VOID_LEVEL_TYPE = "allvoid:all_void";

    public static WorldDataConfiguration enableAllVoid(WorldDataConfiguration configuration) {
        return new WorldDataConfiguration(
                enableAllVoid(configuration.dataPacks()),
                configuration.enabledFeatures()
        );
    }

    public static DataPackConfig enableAllVoid(DataPackConfig configuration) {
        List<String> enabled = new ArrayList<>(configuration.getEnabled());
        List<String> disabled = new ArrayList<>(configuration.getDisabled());
        if (!enabled.contains(ALL_VOID_PACK_ID)) {
            enabled.add(ALL_VOID_PACK_ID);
        }
        disabled.remove(ALL_VOID_PACK_ID);
        return new DataPackConfig(enabled, disabled);
    }

    private AllVoidDataPacks() {
    }
}
