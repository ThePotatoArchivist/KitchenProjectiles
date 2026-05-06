package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import moriyashiine.enchancement.common.ModConfig;
import moriyashiine.enchancement.common.init.ModComponentTypes;
import moriyashiine.enchancement.common.tag.ModItemTags;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(
            method = "getTridentReturnToOwnerAcceleration",
            at = @At("RETURN")
    )
    private static int knifePassiveLoyalty(int original, ServerLevel serverLevel, ItemStack weapon) {
        if (!ModConfig.toggleablePassives || !weapon.is(ModTags.Items.KNIFE_ENCHANTABLE) || weapon.is(ModItemTags.NO_LOYALTY) || !weapon.getOrDefault(ModComponentTypes.TOGGLEABLE_PASSIVE, false)) return original;
        if (!weapon.isEnchanted()) {
            weapon.remove(ModComponentTypes.TOGGLEABLE_PASSIVE);
            return original;
        }
        return 1;
    }
}
