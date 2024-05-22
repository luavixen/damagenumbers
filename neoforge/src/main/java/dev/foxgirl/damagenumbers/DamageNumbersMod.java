package dev.foxgirl.damagenumbers;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.ConfigScreenHandler;

@Mod("damagenumbers")
public final class DamageNumbersMod {

    public DamageNumbersMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        try {
            ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> DamageNumbers.getHandler().createConfigScreen(parent))
            );
        } catch (NoClassDefFoundError ignored) {
        }
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        new DamageNumbers(FMLPaths.CONFIGDIR.get());
    }

}
