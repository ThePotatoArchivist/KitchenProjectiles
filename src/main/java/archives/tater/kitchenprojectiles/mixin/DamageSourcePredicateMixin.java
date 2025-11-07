package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KnifeEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DamageSourcePredicate.class)
public class DamageSourcePredicateMixin {
    @WrapOperation(
            method = "matches(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/damagesource/DamageSource;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;isDirect()Z")
    )
    private boolean allowDirectKnife(DamageSource instance, Operation<Boolean> original) {
        return original.call(instance) || instance.getDirectEntity() instanceof KnifeEntity;
    }
}
