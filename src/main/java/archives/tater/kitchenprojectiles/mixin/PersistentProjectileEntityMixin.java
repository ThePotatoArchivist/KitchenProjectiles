package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KnifeEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;

@Mixin(AbstractArrow.class)
public abstract class PersistentProjectileEntityMixin extends Projectile {
    public PersistentProjectileEntityMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract boolean isNoPhysics();

    @SuppressWarnings("ConstantValue")
    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;isCritArrow()Z"),
            name = "physicsEnabled"
    )
    private boolean hideNoPhysics(boolean physicsEnabled) {
        return physicsEnabled || (Object) this instanceof KnifeEntity knifeEntity && !knifeEntity.hasDealtDamage();
    }

    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;isInWater()Z", ordinal = 1),
            name = "physicsEnabled"
    )
    private boolean restoreNoPhysics(boolean physicsEnabled) {
        return !isNoPhysics();
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "stepMoveAndHit",
            at = @At(value = "INVOKE", target = "Ljava/util/Objects;requireNonNullElse(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            cancellable = true
    )
    private void cancelCollision(BlockHitResult blockHitResult, CallbackInfo ci, @Local(name = "firstEntityHit") EntityHitResult firstEntityHit, @Share("hit")LocalRef<EntityHitResult> hit) {
        if ((Object) this instanceof KnifeEntity knifeEntity && !knifeEntity.hasDealtDamage() && firstEntityHit == null && isNoPhysics()) {
            setPos(position().add(getDeltaMovement()));
            ci.cancel();
        }
        hit.set(firstEntityHit);
    }

    @SuppressWarnings("ConstantValue")
    @ModifyExpressionValue(
            method = "stepMoveAndHit",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;noPhysics:Z", opcode = Opcodes.GETFIELD)
    )
    private boolean allowCollision(boolean original, @Share("hit") LocalRef<EntityHitResult> hit) {
        return original && hit.get() != null && (!((Object) this instanceof KnifeEntity knifeEntity) || knifeEntity.hasDealtDamage());
    }
}
