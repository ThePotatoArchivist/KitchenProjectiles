package archives.tater.kitchenprojectiles;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class KitchenProjectilesUtil {
    private KitchenProjectilesUtil() {}

    public static float getDamage(ItemStack stack, DamageSource source, Level world, Entity target) {
        var instance = new AttributeInstance(Attributes.ATTACK_DAMAGE, ignored -> {});
        instance.setBaseValue(1.0);
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute == Attributes.ATTACK_DAMAGE)
                instance.addTransientModifier(modifier);
        });

        if (!(world instanceof ServerLevel serverWorld)) return (float) instance.getValue();

        return EnchantmentHelper.modifyDamage(serverWorld, stack, target, source, (float) instance.getValue());
    }
}
