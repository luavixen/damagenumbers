package dev.foxgirl.damagenumbers.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.foxgirl.damagenumbers.DamageNumbers;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class Config {

    public boolean isEnabled = true;

    public boolean isPlayerDamageShown = false;

    public Color colorSm = Color.valueOf("#FFAA00");
    public Color colorMd = Color.valueOf("#FF0000");
    public Color colorLg = Color.valueOf("#AA0000");

    public Config() {}

    public boolean optionGetEnabled() {
        return isEnabled;
    }
    public void optionSetEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean optionGetPlayerDamageShown() {
        return isPlayerDamageShown;
    }
    public void optionSetPlayerDamageShown(boolean shown) {
        isPlayerDamageShown = shown;
    }

    public @NotNull java.awt.Color optionGetColorSm() {
        return colorSm.toNativeColor();
    }
    public void optionSetColorSm(@NotNull java.awt.Color color) {
        colorSm = new Color(color);
    }

    public @NotNull java.awt.Color optionGetColorMd() {
        return colorMd.toNativeColor();
    }
    public void optionSetColorMd(@NotNull java.awt.Color color) {
        colorMd = new Color(color);
    }

    public @NotNull java.awt.Color optionGetColorLg() {
        return colorLg.toNativeColor();
    }
    public void optionSetColorLg(@NotNull java.awt.Color color) {
        colorLg = new Color(color);
    }

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .serializeNulls()
            .setPrettyPrinting()
            .setLenient()
            .create();

    public @NotNull String toJson() {
        var json = new JsonObject();
        json.addProperty("isEnabled", isEnabled);
        json.addProperty("isPlayerDamageShown", isPlayerDamageShown);
        json.addProperty("colorSm", colorSm.toString());
        json.addProperty("colorMd", colorMd.toString());
        json.addProperty("colorLg", colorLg.toString());
        return GSON.toJson(json);
    }

    public void fromJson(@NotNull JsonObject json) {
        json.entrySet().forEach(entry -> {
            switch (entry.getKey()) {
                case "isEnabled" -> isEnabled = entry.getValue().getAsBoolean();
                case "isPlayerDamageShown" -> isPlayerDamageShown = entry.getValue().getAsBoolean();
                case "colorSm" -> colorSm = Color.valueOf(entry.getValue().getAsString());
                case "colorMd" -> colorMd = Color.valueOf(entry.getValue().getAsString());
                case "colorLg" -> colorLg = Color.valueOf(entry.getValue().getAsString());
            }
        });
    }

    public interface PathProvider {
        @NotNull Path getConfigFilePath();
        @NotNull Path getConfigTempPath();
    }

    public void readConfig(@NotNull PathProvider paths) {
        try {
            fromJson(GSON.fromJson(Files.newBufferedReader(paths.getConfigFilePath()), JsonObject.class));
        } catch (NoSuchFileException cause) {
            DamageNumbers.LOGGER.error("Failed to read config, file not found, creating config");
            writeConfig(paths);
        } catch (IOException cause) {
            DamageNumbers.LOGGER.error("Failed to read config, IO error", cause);
        } catch (JsonParseException cause) {
            DamageNumbers.LOGGER.error("Failed to read config, JSON error", cause);
        } catch (Exception cause) {
            DamageNumbers.LOGGER.error("Failed to read config", cause);
        }
    }

    public void writeConfig(@NotNull PathProvider paths) {
        try {
            var pathFile = paths.getConfigFilePath();
            var pathTemp = paths.getConfigTempPath();
            Files.writeString(pathTemp, toJson());
            Files.move(pathTemp, pathFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException cause) {
            DamageNumbers.LOGGER.error("Failed to write new config, IO error", cause);
        } catch (Exception cause) {
            DamageNumbers.LOGGER.error("Failed to write new config", cause);
        }
    }

}
