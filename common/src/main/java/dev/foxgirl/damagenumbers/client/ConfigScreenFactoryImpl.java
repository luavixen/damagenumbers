package dev.foxgirl.damagenumbers.client;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public final class ConfigScreenFactoryImpl implements ConfigScreenFactory {

    private final @NotNull Config config;
    private final @NotNull Config configDefault;

    private final @NotNull Config.PathProvider configPathProvider;

    public ConfigScreenFactoryImpl(
        @NotNull Config config,
        @NotNull Config configDefault,
        @NotNull Config.PathProvider configPathProvider
    ) {
        this.config = config;
        this.configDefault = configDefault;
        this.configPathProvider = configPathProvider;
    }

    @Override
    public @NotNull Screen createConfigScreen(@NotNull Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.of("Damage Numbers"))
            .category(ConfigCategory.createBuilder()
                .name(Text.of("Damage Numbers"))
                .option(Option.<Boolean>createBuilder()
                    .name(Text.of("Enabled"))
                    .description(OptionDescription.of(Text.of("Whether or not to display damage numbers.")))
                    .binding(configDefault.optionGetEnabled(), config::optionGetEnabled, config::optionSetEnabled)
                    .controller(BooleanControllerBuilder::create)
                    .build()
                )
                .option(Option.<Boolean>createBuilder()
                    .name(Text.of("Player Damage Shown"))
                    .description(OptionDescription.of(Text.of("Whether or not to display damage numbers for the current player.")))
                    .binding(configDefault.optionGetPlayerDamageShown(), config::optionGetPlayerDamageShown, config::optionSetPlayerDamageShown)
                    .controller(BooleanControllerBuilder::create)
                    .build()
                )
                .option(Option.<java.awt.Color>createBuilder()
                    .name(Text.of("Small Damage Color"))
                    .description(OptionDescription.of(Text.of("Color used for small amounts of damage. (below 2 damage, mixed up to 8)")))
                    .binding(configDefault.optionGetColorSm(), config::optionGetColorSm, config::optionSetColorSm)
                    .controller(ColorControllerBuilder::create)
                    .build()
                )
                .option(Option.<java.awt.Color>createBuilder()
                    .name(Text.of("Medium Damage Color"))
                    .description(OptionDescription.of(Text.of("Color used for medium amounts of damage. (mixed between 2 and 16 damage)")))
                    .binding(configDefault.optionGetColorMd(), config::optionGetColorMd, config::optionSetColorMd)
                    .controller(ColorControllerBuilder::create)
                    .build()
                )
                .option(Option.<java.awt.Color>createBuilder()
                    .name(Text.of("Large Damage Color"))
                    .description(OptionDescription.of(Text.of("Color used for large amounts of damage. (above 16 damage, mixed down to 8)")))
                    .binding(configDefault.optionGetColorLg(), config::optionGetColorLg, config::optionSetColorLg)
                    .controller(ColorControllerBuilder::create)
                    .build()
                )
                .build()
            )
            .save(() -> config.writeConfig(configPathProvider))
            .build()
            .generateScreen(parent);
    }

}
