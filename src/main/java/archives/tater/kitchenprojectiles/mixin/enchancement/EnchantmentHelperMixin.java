package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import moriyashiine.enchancement.common.EnchancementConfig;
import moriyashiine.enchancement.common.init.EnchancementDataComponents;
import moriyashiine.enchancement.common.tag.EnchancementItemTags;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(
            method = "getTridentReturnToOwnerAcceleration",
            at = @At("RETURN")
    )
    private static int knifePassiveLoyalty(int original, ServerLevel serverLevel, ItemStack weapon) {
        if (!EnchancementConfig.toggleablePassives || !weapon.is(ModTags.Items.KNIFE_ENCHANTABLE) || weapon.is(EnchancementItemTags.NO_LOYALTY) || !weapon.getOrDefault(EnchancementDataComponents.TOGGLEABLE_PASSIVE, false)) return original;
        if (!weapon.isEnchanted()) {
            weapon.remove(EnchancementDataComponents.TOGGLEABLE_PASSIVE);
            return original;
        }
        return 1;
    }
}
