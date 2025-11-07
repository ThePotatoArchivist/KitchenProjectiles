package archives.tater.kitchenprojectiles.mixin.client.enchancement;

import moriyashiine.enchancement.client.event.ToggleablePassivesEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.Map;

@Mixin(value = ToggleablePassivesEvent.class, remap = false)
public class ToggleablePassivesEventMixin {
    @Shadow
    @Final
    private static Map<TagKey<Item>, String> KEY_MAP;

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void addKnives(CallbackInfo ci) {
        KEY_MAP.put(ModTags.KNIFE_ENCHANTABLE, "tooltip.enchancement.has_loyalty");
    }
}
