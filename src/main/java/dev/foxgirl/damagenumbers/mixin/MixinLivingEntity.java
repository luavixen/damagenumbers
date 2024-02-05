package dev.foxgirl.damagenumbers.mixin;

import dev.foxgirl.damagenumbers.DamageNumbers;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Unique
    private float previousHealth = 0.0F;

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        var entity = (LivingEntity) (Object) this;

        var world = entity.getWorld();
        if (world == null || !world.isClient()) return;

        float oldHealth = previousHealth;
        float newHealth = entity.getHealth();

        if (oldHealth != newHealth) {
            previousHealth = newHealth;
            DamageNumbers
                .getInstance()
                .onEntityHealthChange(entity, oldHealth, newHealth);
        }
    }

}
