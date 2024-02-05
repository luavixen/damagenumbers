package dev.foxgirl.damagenumbers;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

public record Color(float r, float g, float b, float a) {

    public Color(int value) {
        this(
            Math.max(0.0F, (float) (value >> 16 & 0xFF) / 255.0F),
            Math.max(0.0F, (float) (value >>  8 & 0xFF) / 255.0F),
            Math.max(0.0F, (float) (value       & 0xFF) / 255.0F),
            Math.max(0.0F, (float) (value >> 24 & 0xFF) / 255.0F)
        );
    }

    public Color(@NotNull java.awt.Color color) {
        this(color.getRGB());
    }

    public int getValue() {
        int value = 0;
        value |= convertComponent(a) << 24;
        value |= convertComponent(r) << 16;
        value |= convertComponent(g) << 8;
        value |= convertComponent(b);
        return value;
    }

    public @NotNull java.awt.Color toNativeColor() {
        return new java.awt.Color(getValue(), true);
    }

    @Override
    public @NotNull String toString() {
        if (a == 1.0F) {
            return String.format(
                "#%02X%02X%02X",
                convertComponent(r),
                convertComponent(g),
                convertComponent(b)
            );
        } else {
            return String.format(
                "#%02X%02X%02X%02X",
                convertComponent(r),
                convertComponent(g),
                convertComponent(b),
                convertComponent(a)
            );
        }
    }

    private static final Pattern pattern = Pattern.compile(
        "#?([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})?",
        Pattern.CASE_INSENSITIVE
    );

    public static @NotNull Color valueOf(@NotNull String string) {
        Objects.requireNonNull(string, "Argument 'string'");

        var matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid color code syntax");
        }

        int value;

        var alpha = matcher.group(4);
        if (alpha != null) {
            value = parseHexComponent(alpha) << 24;
        } else {
            value = 0xFF000000;
        }

        value |= parseHexComponent(matcher.group(1)) << 16;
        value |= parseHexComponent(matcher.group(2)) << 8;
        value |= parseHexComponent(matcher.group(3));

        return new Color(value);
    }

    public static @NotNull Color lerp(@NotNull Color a, @NotNull Color b, float delta) {
        Objects.requireNonNull(a, "Argument 'a'");
        Objects.requireNonNull(b, "Argument 'b'");
        return new Color(
            a.r + (b.r - a.r) * delta,
            a.g + (b.g - a.g) * delta,
            a.b + (b.b - a.b) * delta,
            a.a + (b.a - a.a) * delta
        );
    }

    private static int parseHexComponent(String input) {
        return Integer.parseUnsignedInt(input, 16);
    }

    private static int convertComponent(float component) {
        return Math.max(Math.min((int) (component * 255.0F), 0xFF), 0);
    }

}
