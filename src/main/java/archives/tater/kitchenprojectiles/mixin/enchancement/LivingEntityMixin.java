package archives.tater.kitchenprojectiles.mixin.enchancement;

import archives.tater.kitchenprojectiles.KnifeEntity;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import moriyashiine.enchancement.common.world.item.effects.RageEffect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyVariable(
            method = "hurtServer", at = @At("HEAD"),
            argsOnly = true,
            name = "damage"
    )
    private float applyKnifeRage(float damage, ServerLevel level, DamageSource source) {
        if (!(source.getDirectEntity() instanceof KnifeEntity knifeEntity) || !(knifeEntity.getOwner() instanceof LivingEntity livingEntity) || knifeEntity.level().isClientSide())
            return damage;
        return damage + RageEffect.getDamageDealtModifier(livingEntity, knifeEntity.getPickupItemStackOrigin());
    }
}
