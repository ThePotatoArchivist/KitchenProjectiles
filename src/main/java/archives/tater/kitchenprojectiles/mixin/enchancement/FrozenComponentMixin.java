package archives.tater.kitchenprojectiles.mixin.enchancement;

import archives.tater.kitchenprojectiles.KnifeEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.component.entity.FrozenComponent;
import moriyashiine.enchancement.common.tag.ModEnchantmentTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FrozenComponent.class, remap = false)
public class FrozenComponentMixin {
    @ModifyReturnValue(
            method = "isSourceFrostbiteWeapon",
            at = @At("RETURN")
    )
    private static boolean checkKnife(boolean original, @Local(argsOnly = true) DamageSource source) {
        return original || source.getDirectEntity() instanceof KnifeEntity knifeEntity && EnchantmentHelper.hasTag(knifeEntity.getPickupItemStackOrigin(), ModEnchantmentTags.FREEZES_ENTITIES);
    }
}
