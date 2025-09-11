package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.event.config.RebalanceEquipmentEvent;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(RebalanceEquipmentEvent.class)
public class RebalanceEquipmentEventMixin {
    @ModifyReturnValue(
            method = "isValid",
            at = @At("RETURN")
    )
    private static boolean allowKnife(boolean original, @Local(argsOnly = true) PlayerEntity player) {
        return original || player.getActiveItem().isIn(ModTags.KNIVES);
    }
}
