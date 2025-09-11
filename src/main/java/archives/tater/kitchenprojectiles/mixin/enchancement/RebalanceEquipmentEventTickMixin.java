package archives.tater.kitchenprojectiles.mixin.enchancement;

import archives.tater.kitchenprojectiles.KitchenProjectiles;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.event.config.RebalanceEquipmentEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(value = RebalanceEquipmentEvent.Tick.class, remap = false)
public class RebalanceEquipmentEventTickMixin {
    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lmoriyashiine/enchancement/common/util/EnchancementUtil;getTridentChargeTime()I")
    )
    private int useKnifeTime(int original, @Local(argsOnly = true) Entity entity) {
        return entity instanceof LivingEntity livingEntity && livingEntity.getActiveItem().isIn(ModTags.KNIVES) ? KitchenProjectiles.MIN_USE_DURATION : original;
    }
}
