package dev.foxgirl.damagenumbers;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("damagenumbers")
public final class DamageNumbersMod {

    public DamageNumbersMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> DamageNumbers.getHandler().createConfigScreen(parent))
        );
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        new DamageNumbers(FMLPaths.CONFIGDIR.get());
    }

}
