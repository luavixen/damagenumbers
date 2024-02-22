package dev.foxgirl.damagenumbers;

import dev.foxgirl.damagenumbers.client.DamageNumbersHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class DamageNumbers {

    public static @NotNull Logger LOGGER = LogManager.getLogger();

    private static DamageNumbers INSTANCE = null;

    public static @NotNull DamageNumbersHandler getHandler() {
        if (INSTANCE == null) {
            throw new IllegalStateException("DamageNumbers not initialized");
        }
        return INSTANCE.handler;
    }

    private final @NotNull DamageNumbersHandler handler;

    public DamageNumbers(@NotNull Path configDir) {
        INSTANCE = this;
        try {
            handler = (DamageNumbersHandler) Class
                .forName("dev.foxgirl.damagenumbers.client.DamageNumbersImpl")
                .getConstructor(Path.class)
                .newInstance(configDir);
        } catch (ReflectiveOperationException cause) {
            throw new RuntimeException("Failed to initialize DamageNumbers handler", cause);
        }
    }

}
