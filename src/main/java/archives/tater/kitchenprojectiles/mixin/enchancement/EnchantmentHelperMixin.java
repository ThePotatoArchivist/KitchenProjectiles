package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.ModConfig;
import moriyashiine.enchancement.common.init.ModComponentTypes;
import moriyashiine.enchancement.common.tag.ModItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getTridentReturnToOwnerAcceleration", at = @At("RETURN"))
    private static int knifePassiveLoyalty(int original, @Local(argsOnly = true) ItemStack stack) {
        if (!ModConfig.toggleablePassives || !stack.is(ModTags.KNIFE_ENCHANTABLE) || stack.is(ModItemTags.NO_LOYALTY) || !stack.getOrDefault(ModComponentTypes.TOGGLEABLE_PASSIVE, false)) return original;
        if (!stack.isEnchanted()) {
            stack.remove(ModComponentTypes.TOGGLEABLE_PASSIVE);
            return original;
        }
        return 1;
    }
}
