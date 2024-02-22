package dev.foxgirl.damagenumbers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class DamageNumbersInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new DamageNumbers(FabricLoader.getInstance().getConfigDir());
    }

}
