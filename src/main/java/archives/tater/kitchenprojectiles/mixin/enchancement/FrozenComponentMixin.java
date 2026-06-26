package archives.tater.kitchenprojectiles.mixin.enchancement;

import archives.tater.kitchenprojectiles.ThrownKnife;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import moriyashiine.enchancement.common.component.entity.enchantmenteffecttype.FrozenComponent;
import moriyashiine.enchancement.common.tag.EnchancementEnchantmentTags;

@Mixin(value = FrozenComponent.class)
public class FrozenComponentMixin {
    @ModifyReturnValue(
            method = "isSourceFreezeWeapon",
            at = @At("RETURN")
    )
    private static boolean checkKnife(boolean original, DamageSource source) {
        return original || source.getDirectEntity() instanceof ThrownKnife thrownKnife && EnchantmentHelper.hasTag(thrownKnife.getPickupItemStackOrigin(), EnchancementEnchantmentTags.FREEZES_ENTITIES);
    }
}
