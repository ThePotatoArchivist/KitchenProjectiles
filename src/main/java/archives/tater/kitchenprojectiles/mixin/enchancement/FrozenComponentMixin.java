package archives.tater.kitchenprojectiles.mixin.enchancement;

import archives.tater.kitchenprojectiles.ThrownKnife;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import moriyashiine.enchancement.common.component.entity.enchantmenteffecttype.FrozenComponent;
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
    private static boolean checkKnife(boolean original, DamageSource source) {
        return original || source.getDirectEntity() instanceof ThrownKnife thrownKnife && EnchantmentHelper.hasTag(thrownKnife.getPickupItemStackOrigin(), ModEnchantmentTags.FREEZES_ENTITIES);
    }
}
