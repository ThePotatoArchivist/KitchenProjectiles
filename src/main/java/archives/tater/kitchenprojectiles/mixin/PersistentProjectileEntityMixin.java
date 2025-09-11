package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KnifeEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {
    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean isNoClip();

    @SuppressWarnings("ConstantValue")
    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isCritical()Z")
    )
    private boolean hideNoClip(boolean value) {
        return value || (Object) this instanceof KnifeEntity knifeEntity && !knifeEntity.hasDealtDamage();
    }

    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isTouchingWater()Z", ordinal = 1)
    )
    private boolean restoreNoClip(boolean value) {
        return !isNoClip();
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "applyCollision",
            at = @At(value = "INVOKE", target = "Ljava/util/Objects;requireNonNullElse(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            cancellable = true
    )
    private void cancelCollision(BlockHitResult blockHitResult, CallbackInfo ci, @Local EntityHitResult entityHitResult) {
        if ((Object) this instanceof KnifeEntity knifeEntity && !knifeEntity.hasDealtDamage() && entityHitResult == null && isNoClip()) {
            setPosition(getPos().add(getVelocity()));
            ci.cancel();
        }
    }

    @SuppressWarnings("ConstantValue")
    @ModifyExpressionValue(
            method = "applyCollision",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;noClip:Z")
    )
    private boolean allowCollision(boolean original, @Local EntityHitResult entityHitResult) {
        return original && entityHitResult != null && (!((Object) this instanceof KnifeEntity knifeEntity) || knifeEntity.hasDealtDamage());
    }
}
