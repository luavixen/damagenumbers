package dev.foxgirl.damagenumbers.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface DamageNumbersHandler {

    @NotNull Screen createConfigScreen(@NotNull Screen parent);

    void onEntityHealthChange(@NotNull LivingEntity entity, float oldHealth, float newHealth);

}
