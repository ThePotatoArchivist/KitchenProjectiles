package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moriyashiine.enchancement.common.ModConfig;
import moriyashiine.enchancement.common.init.ModComponentTypes;
import moriyashiine.enchancement.common.tag.ModItemTags;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @ModifyReturnValue(method = "getTridentReturnAcceleration", at = @At("RETURN"))
    private static int enchancement$toggleablePassives(int original, ServerWorld world, ItemStack stack) {
        if (!ModConfig.toggleablePassives || !stack.isIn(ModTags.KNIFE_ENCHANTABLE) || stack.isIn(ModItemTags.NO_LOYALTY) || !stack.getOrDefault(ModComponentTypes.TOGGLEABLE_PASSIVE, false)) return original;
        if (!stack.hasEnchantments()) {
            stack.remove(ModComponentTypes.TOGGLEABLE_PASSIVE);
            return original;
        }
        return 1;
    }
}
