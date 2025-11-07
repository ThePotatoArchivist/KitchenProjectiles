package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KnifeEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractArrow.class)
public abstract class PersistentProjectileEntityMixin {
    @Shadow
    public abstract boolean isNoPhysics();

    @SuppressWarnings("ConstantValue")
    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;isRemoved()Z")
    )
    private boolean hideNoClip(boolean value) {
        return value && (!((Object) this instanceof KnifeEntity knifeEntity) || knifeEntity.hasDealtDamage());
    }

    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V")
    )
    private boolean restoreNoClip(boolean value) {
        return isNoPhysics();
    }
}
