package archives.tater.kitchenprojectiles.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.LoyaltyEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.common.item.KnifeItem;

@Mixin(LoyaltyEnchantment.class)
public abstract class LoyaltyEnchantmentMixin extends Enchantment {

    protected LoyaltyEnchantmentMixin(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    // I intended to mixin class Enchantment but it repeatedly refused to work
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof KnifeItem || super.isAcceptableItem(stack);
    }
}
