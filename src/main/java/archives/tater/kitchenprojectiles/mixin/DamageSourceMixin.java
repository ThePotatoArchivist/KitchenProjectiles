package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.ThrownKnife;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

@Mixin(DamageSource.class)
public class DamageSourceMixin {
    @Shadow
    @Final
    @Nullable
    private Entity directEntity;

    @ModifyVariable(
            method = "getLocalizedDeathMessage",
            at = @At("STORE"),
            name = "held"
    )
    private ItemStack useKnifeStack(ItemStack held) {
        return directEntity instanceof ThrownKnife thrownKnife ? thrownKnife.getPickupItemStackOrigin() : held;
    }
}
