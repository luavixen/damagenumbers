package dev.foxgirl.damagenumbers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod("damagenumbers")
public final class DamageNumbersMod {

    public DamageNumbersMod(IEventBus eventBus) {
        eventBus.addListener(this::onClientSetup);
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, parent) -> DamageNumbers.getHandler().createConfigScreen(parent));
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        new DamageNumbers(FMLPaths.CONFIGDIR.get());
    }

}
