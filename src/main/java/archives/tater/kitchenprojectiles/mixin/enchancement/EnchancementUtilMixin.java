package archives.tater.kitchenprojectiles.mixin.enchancement;

import archives.tater.kitchenprojectiles.KitchenProjectiles;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.item.ItemStack;

import moriyashiine.enchancement.common.util.EnchancementUtil;

@Mixin(EnchancementUtil.class)
public class EnchancementUtilMixin {
    @ModifyReturnValue(
            method = "isFastItem",
            at = @At("RETURN:FIRST")
    )
    private static boolean fastKnife(boolean original, ItemStack stack) {
        return original || stack.is(KitchenProjectiles.THROWABLE_KNIVES);
    }
}
