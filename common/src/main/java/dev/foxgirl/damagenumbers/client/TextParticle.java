package dev.foxgirl.damagenumbers.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public final class TextParticle extends Particle {

    private String text;

    public TextParticle(ClientWorld world, Vec3d pos, Vec3d velocity) {
        super(world, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        velocityMultiplier = 0.99F;
        gravityStrength = 0.75F;
        maxAge = 32;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }

    public void setColor(@NotNull Color color) {
        this.red = color.r();
        this.green = color.g();
        this.blue = color.b();
        this.alpha = color.a();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        var cameraPos = camera.getPos();

        float particleX = (float) (prevPosX + (x - prevPosX) * (double) tickDelta - cameraPos.x);
        float particleY = (float) (prevPosY + (y - prevPosY) * (double) tickDelta - cameraPos.y);
        float particleZ = (float) (prevPosZ + (z - prevPosZ) * (double) tickDelta - cameraPos.z);

        Matrix4f matrix = new Matrix4f();
        matrix = matrix.translation(particleX, particleY, particleZ);
        matrix = matrix.rotate(camera.getRotation());
        matrix = matrix.scale(-0.025F, -0.025F, 0.025F);

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var vertexConsumerProvider = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

        float textX = textRenderer.getWidth(text) / -2.0F;
        float textY = 0.0F;

        int textColor = new Color(red, green, blue, alpha).getValue();

        textRenderer.draw(text, textX, textY, textColor, false, matrix, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        vertexConsumerProvider.draw();
    }

}
