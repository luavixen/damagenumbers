package dev.foxgirl.damagenumbers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;

public final class Config {

    public Color colorSm = Color.valueOf("#FFAA00");
    public Color colorMd = Color.valueOf("#FF0000");
    public Color colorLg = Color.valueOf("#AA0000");

    public Config() {}

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
        json.addProperty("colorSm", colorSm.toString());
        json.addProperty("colorMd", colorMd.toString());
        json.addProperty("colorLg", colorLg.toString());
        return GSON.toJson(json);
    }

    public void fromJson(@NotNull JsonObject json) {
        json.entrySet().forEach(e -> {
            switch (e.getKey()) {
                case "colorSm" -> colorSm = Color.valueOf(e.getValue().getAsString());
                case "colorMd" -> colorMd = Color.valueOf(e.getValue().getAsString());
                case "colorLg" -> colorLg = Color.valueOf(e.getValue().getAsString());
            }
        });
    }

    public void readConfig() {
        try {
            fromJson(GSON.fromJson(Files.newBufferedReader(DamageNumbers.getInstance().configFilePath()), JsonObject.class));
        } catch (NoSuchFileException cause) {
            DamageNumbers.LOGGER.error("Failed to read config, file not found, creating config");
            writeConfig();
        } catch (IOException cause) {
            DamageNumbers.LOGGER.error("Failed to read config, IO error", cause);
        } catch (JsonParseException cause) {
            DamageNumbers.LOGGER.error("Failed to read config, JSON error", cause);
        } catch (Exception cause) {
            DamageNumbers.LOGGER.error("Failed to read config", cause);
        }
    }

    public void writeConfig() {
        try {
            var pathFile = DamageNumbers.getInstance().configFilePath();
            var pathTemp = DamageNumbers.getInstance().configTempPath();
            Files.writeString(pathTemp, toJson());
            Files.move(pathTemp, pathFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException cause) {
            DamageNumbers.LOGGER.error("Failed to write new config, IO error", cause);
        } catch (Exception cause) {
            DamageNumbers.LOGGER.error("Failed to write new config", cause);
        }
    }

}
