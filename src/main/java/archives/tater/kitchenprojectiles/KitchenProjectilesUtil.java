package archives.tater.kitchenprojectiles;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class KitchenProjectilesUtil {
    private KitchenProjectilesUtil() {}

    public static float getDamage(ItemStack stack, DamageSource source, World world, Entity target) {
        var instance = new EntityAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE, ignored -> {});
        instance.setBaseValue(1.0);
        stack.applyAttributeModifiers(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE)
                instance.addTemporaryModifier(modifier);
        });

        if (!(world instanceof ServerWorld serverWorld)) return (float) instance.getValue();

        return EnchantmentHelper.getDamage(serverWorld, stack, target, source, (float) instance.getValue());
    }
}
