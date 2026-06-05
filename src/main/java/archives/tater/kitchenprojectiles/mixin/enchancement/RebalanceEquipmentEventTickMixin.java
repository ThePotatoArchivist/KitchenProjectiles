package archives.tater.kitchenprojectiles.mixin.enchancement;

import archives.tater.kitchenprojectiles.KitchenProjectiles;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.event.config.RebalanceEquipmentEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(targets = "moriyashiine/enchancement/common/event/config/RebalanceEquipmentEvent$Tick")
public class RebalanceEquipmentEventTickMixin {
    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lmoriyashiine/enchancement/common/util/EnchancementUtil;getMaceOrTridentChargeTime(Lnet/minecraft/world/item/ItemStack;)I")
    )
    private int useKnifeTime(int original, Level level, Entity entity) {
        return entity instanceof LivingEntity livingEntity && livingEntity.getUseItem().is(ModTags.Items.KNIVES) ? KitchenProjectiles.MIN_USE_DURATION : original;
    }
}
