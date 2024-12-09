package archives.tater.kitchenprojectiles.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.LoyaltyEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.item.KnifeItem;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    // This doesn't work for some reason???
    @Inject(
            method = "isAcceptableItem",
            at = @At(value = "HEAD"), cancellable = true)
    private void allowLoyaltyOnKnife(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        //noinspection ConstantValue
        if (((Object) this) instanceof LoyaltyEnchantment && stack.getItem() instanceof KnifeItem)
            cir.setReturnValue(true);
    }
}
