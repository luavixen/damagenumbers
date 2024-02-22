package dev.foxgirl.damagenumbers.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.foxgirl.damagenumbers.DamageNumbers;

public final class DamageNumbersModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> DamageNumbers.getHandler().createConfigScreen(parent);
    }

}
