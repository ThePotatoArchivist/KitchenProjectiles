package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KnifeEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {
    @Shadow
    public abstract boolean isNoClip();

    @SuppressWarnings("ConstantValue")
    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isRemoved()Z")
    )
    private boolean hideNoClip(boolean value) {
        return (!((Object) this instanceof KnifeEntity knifeEntity) || knifeEntity.hasDealtDamage()) && value;
    }

    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V")
    )
    private boolean restoreNoClip(boolean value) {
        return isNoClip();
    }
}
