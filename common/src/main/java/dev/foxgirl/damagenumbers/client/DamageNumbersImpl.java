package dev.foxgirl.damagenumbers.client;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class DamageNumbersImpl implements DamageNumbersHandler, Config.PathProvider {

    public final @NotNull Config config = new Config();
    public final @NotNull Config configDefault = new Config();

    public final @NotNull Path configDirPath;

    @Override
    public @NotNull Path getConfigFilePath() {
        return configDirPath.resolve("damagenumbers.json");
    }
    @Override
    public @NotNull Path getConfigTempPath() {
        return configDirPath.resolve("damagenumbers.json.tmp");
    }

    public DamageNumbersImpl(@NotNull Path configDir) {
        configDirPath = configDir;
        config.readConfig(this);
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
            .save(() -> config.writeConfig(this))
            .build()
            .generateScreen(parent);
    }

    public void onEntityHealthChange(@NotNull LivingEntity entity, float oldHealth, float newHealth) {
        if (!config.isEnabled) return;

        float damage = oldHealth - newHealth;
        if (damage <= 0.0F) return;

        var client = MinecraftClient.getInstance();

        if (entity == client.player && !config.isPlayerDamageShown) return;

        var world = client.world;
        if (world == null || world != entity.getWorld()) return;

        Vec3d particlePos = entity.getPos().add(0.0, entity.getHeight() + 0.25, 0.0);

        Vec3d particleVelocity = entity.getVelocity();

        Vec3d particleVelocityForward = entity.getPos();
        particleVelocityForward = particleVelocityForward.subtract(client.gameRenderer.getCamera().getPos()).normalize();
        particleVelocityForward = particleVelocityForward.multiply(entity.getWidth() * 10.0);

        particleVelocity = particleVelocity.subtract(particleVelocityForward.x, -20.0, particleVelocityForward.z);

        var particle = new TextParticle(world, particlePos, particleVelocity);

        var text = String.format("%.1f", damage);
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }

        particle.setText(text);

        if (damage >= 16.0F) {
            particle.setColor(config.colorLg);
        } else if (damage >= 8.0F) {
            particle.setColor(Color.lerp(config.colorMd, config.colorLg, (damage - 8.0F) / 8.0F));
        } else if (damage >= 2.0F) {
            particle.setColor(Color.lerp(config.colorSm, config.colorMd, (damage - 2.0F) / 6.0F));
        } else {
            particle.setColor(config.colorSm);
        }

        client.particleManager.addParticle(particle);
    }

}
