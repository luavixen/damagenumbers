package dev.foxgirl.damagenumbers;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@Mod("damagenumbers")
public final class DamageNumbers {

    public static @NotNull Logger LOGGER = LogManager.getLogger();

    private static DamageNumbers INSTANCE;

    public static @NotNull DamageNumbers getInstance() {
        return INSTANCE;
    }

    public static @NotNull MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public DamageNumbers() {
        INSTANCE = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> createConfigScreen(parent))
        );
    }

    public final @NotNull Config config = new Config();
    public final @NotNull Config configDefault = new Config();

    public @Nullable Path configDirPath = null;

    public @NotNull Path configFilePath() {
        return configDirPath.resolve("damagenumbers.json");
    }
    public @NotNull Path configTempPath() {
        return configDirPath.resolve("damagenumbers.json.tmp");
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        configDirPath = FMLPaths.CONFIGDIR.get();
        config.readConfig();
    }

    public @NotNull Screen createConfigScreen(@NotNull Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.of("Damage Numbers"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Damage Numbers"))
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
                .save(config::writeConfig)
                .build()
                .generateScreen(parent);
    }

    public void onEntityHealthChange(@NotNull LivingEntity entity, float oldHealth, float newHealth) {
        float damage = oldHealth - newHealth;
        if (damage <= 0.0F) return;

        var client = getClient();

        var world = client.world;
        if (world == null || world != entity.getWorld()) return;

        Vec3d particlePos = entity.getPos().add(0.0, entity.getHeight() + 0.25, 0.0);

        Vec3d particleVelocity = entity.getVelocity();

        Vec3d particleVelocityForward = entity.getPos();
        particleVelocityForward = particleVelocityForward.subtract(client.gameRenderer.getCamera().getPos()).normalize();
        particleVelocityForward = particleVelocityForward.multiply(entity.getWidth() * 10.0);

        particleVelocity = particleVelocity.subtract(particleVelocityForward.x, -20.0, particleVelocityForward.z);

        var particle = new DamageNumberParticle(world, particlePos, particleVelocity);

        if (damage % 1.0F == 0.0F) {
            particle.setText(String.format("%.0f", damage));
        } else {
            particle.setText(String.format("%.1f", damage));
        }

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
