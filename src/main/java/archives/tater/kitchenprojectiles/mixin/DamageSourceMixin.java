package archives.tater.kitchenprojectiles.mixin;

import archives.tater.kitchenprojectiles.KnifeEntity;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

@Debug(export = true)
@Mixin(DamageSource.class)
public class DamageSourceMixin {
    @Shadow
    @Final
    @Nullable
    private Entity directEntity;

    @ModifyVariable(
            method = "getLocalizedDeathMessage",
            at = @At("STORE"),
            ordinal = 0
    )
    private ItemStack useKnifeStack(ItemStack value) {
        return directEntity instanceof KnifeEntity knifeEntity ? knifeEntity.getPickupItemStackOrigin() : value;
    }
}
