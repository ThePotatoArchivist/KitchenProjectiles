package archives.tater.kitchenprojectiles.mixin.enchancement;

import archives.tater.kitchenprojectiles.KnifeEntity;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.enchantment.effect.RageEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float applyKnifeRage(float value, @Local(argsOnly = true) DamageSource source) {
        if (!(source.getDirectEntity() instanceof KnifeEntity knifeEntity) || !(knifeEntity.getOwner() instanceof LivingEntity livingEntity) || knifeEntity.level().isClientSide)
            return value;
        return value + RageEffect.getDamageDealtModifier(livingEntity, knifeEntity.getPickupItemStackOrigin());
    }
}
