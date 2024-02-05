package dev.foxgirl.damagenumbers;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class DamageNumbersModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> DamageNumbers.getInstance().createConfigScreen(parent);
    }

}
