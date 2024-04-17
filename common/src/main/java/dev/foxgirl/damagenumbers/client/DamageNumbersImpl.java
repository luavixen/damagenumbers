package dev.foxgirl.damagenumbers.client;

import dev.foxgirl.damagenumbers.DamageNumbers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
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
        try {
            var factory = (ConfigScreenFactory) Class
                .forName("dev.foxgirl.damagenumbers.client.ConfigScreenFactoryImpl")
                .getConstructor(Config.class, Config.class, Config.PathProvider.class)
                .newInstance(config, configDefault, this);
            return factory.createConfigScreen(parent);
        } catch (NoClassDefFoundError cause) {
            DamageNumbers.LOGGER.error("Failed to create config screen due to missing class", cause);
        } catch (ReflectiveOperationException cause) {
            DamageNumbers.LOGGER.error("Failed to create config screen due to reflection error", cause);
        }
        return new NoticeScreen(
            () -> MinecraftClient.getInstance().setScreen(parent),
            Text.of("Config screen unavailable"),
            Text.of("YACL3 (Yet Another Config Library 3) may not be installed.\nPlease install a supported version to use the Damage Numbers config screen.")
        );
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
