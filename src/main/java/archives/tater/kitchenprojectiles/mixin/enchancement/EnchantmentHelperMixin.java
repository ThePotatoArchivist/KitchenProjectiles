package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.ModConfig;
import moriyashiine.enchancement.common.init.ModComponentTypes;
import moriyashiine.enchancement.common.tag.ModItemTags;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getTridentReturnAcceleration", at = @At("RETURN"))
    private static int knifePassiveLoyalty(int original, @Local(argsOnly = true) ItemStack stack) {
        if (!ModConfig.toggleablePassives || !stack.isIn(ModTags.KNIFE_ENCHANTABLE) || stack.isIn(ModItemTags.NO_LOYALTY) || !stack.getOrDefault(ModComponentTypes.TOGGLEABLE_PASSIVE, false)) return original;
        if (!stack.hasEnchantments()) {
            stack.remove(ModComponentTypes.TOGGLEABLE_PASSIVE);
            return original;
        }
        return 1;
    }
}
