package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moriyashiine.enchancement.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.condition.RandomChanceWithEnchantedBonusLootCondition;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RandomChanceWithEnchantedBonusLootCondition.class)
public class RandomChanceWithEnchantedBonusLootConditionMixin {
    @WrapOperation(
            method = "method_952",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/RegistryWrapper$Impl;getOrThrow(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/registry/entry/RegistryEntry$Reference;")
    )
    private static RegistryEntry.Reference<Enchantment> handleMissingLooting(RegistryWrapper.Impl<Enchantment> instance, RegistryKey<Enchantment> registryKey, Operation<RegistryEntry.Reference<Enchantment>> original) {
        return instance.getOptional(registryKey).orElse(instance.getOrThrow(ModEnchantments.EMPTY_KEY));
    }
}
