package dev.foxgirl.damagenumbers;

import dev.foxgirl.damagenumbers.client.DamageNumbersHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class DamageNumbers implements ClientModInitializer {

    public static @NotNull Logger LOGGER = LogManager.getLogger();

    private static DamageNumbersHandler HANDLER = null;

    private static void initializeHandler(@NotNull Path configDir) {
        try {
            HANDLER = (DamageNumbersHandler) Class
                .forName("dev.foxgirl.damagenumbers.client.DamageNumbersImpl")
                .getConstructor(Path.class)
                .newInstance(configDir);
        } catch (ReflectiveOperationException cause) {
            throw new RuntimeException("Failed to initialize DamageNumbers handler", cause);
        }
    }

    public static @NotNull DamageNumbersHandler getHandler() {
        if (HANDLER == null) {
            throw new IllegalStateException("DamageNumbers handler not initialized");
        }
        return HANDLER;
    }

    @Override
    public void onInitializeClient() {
        initializeHandler(FabricLoader.getInstance().getConfigDir());
    }

}
