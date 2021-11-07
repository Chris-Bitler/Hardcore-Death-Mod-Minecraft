package io.cbitler.deathplugin.mixins;

import io.cbitler.deathplugin.init.ConfigInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin disables the speed effect as some mods can apparently
 * cause entities to reach hyperspeed with speed given to entities in HARD difficulty
 */
@Mixin(LivingEntity.class)
public abstract class SpeedMixin {
    @Shadow public abstract boolean removeStatusEffect(StatusEffect type);

    @Inject(at = @At("RETURN"), method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z")
    public void stopSpeed(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (ConfigInit.CONFIG.enableSpeedDisableMixin) {
            if (effect.getEffectType() == StatusEffects.SPEED) {
                this.removeStatusEffect(StatusEffects.SPEED);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z")
    public void stopSpeed(StatusEffectInstance effect, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ConfigInit.CONFIG.enableSpeedDisableMixin) {
            if (effect.getEffectType() == StatusEffects.SPEED) {
                this.removeStatusEffect(StatusEffects.SPEED);
            }
        }
    }
}
