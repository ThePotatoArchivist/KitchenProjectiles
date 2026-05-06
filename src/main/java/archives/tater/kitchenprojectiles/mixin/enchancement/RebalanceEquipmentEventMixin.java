package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.event.config.RebalanceEquipmentEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.tag.ModTags;

@Mixin(value = RebalanceEquipmentEvent.class, remap = false)
public class RebalanceEquipmentEventMixin {
    @ModifyReturnValue(
            method = "isMaceOrTrident",
            at = @At("RETURN")
    )
    private static boolean allowKnife(boolean original, Player player) {
        return original || player.getUseItem().is(ModTags.Items.KNIVES);
    }
}
