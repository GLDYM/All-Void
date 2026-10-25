package dev.polaris_light.allvoid;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Client-only registration for the NeoForge configuration screen. */
@EventBusSubscriber(modid = AllVoid.MOD_ID, value = Dist.CLIENT)
public final class AllVoidClient {
    private AllVoidClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModList.get().getModContainerById(AllVoid.MOD_ID).orElseThrow()
                .registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
