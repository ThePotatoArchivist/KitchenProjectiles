package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KnifeEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DamageSourcePredicate.class)
public class DamageSourcePredicateMixin {
    @WrapOperation(
            method = "test(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/damage/DamageSource;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isDirect()Z")
    )
    private boolean allowDirectKnife(DamageSource instance, Operation<Boolean> original) {
        return original.call(instance) || instance.getSource() instanceof KnifeEntity;
    }
}
