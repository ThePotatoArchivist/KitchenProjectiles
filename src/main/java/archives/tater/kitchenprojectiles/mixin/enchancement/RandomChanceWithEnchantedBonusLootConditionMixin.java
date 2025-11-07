package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moriyashiine.enchancement.common.init.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootItemRandomChanceWithEnchantedBonusCondition.class)
public class RandomChanceWithEnchantedBonusLootConditionMixin {
    @WrapOperation(
            method = "method_952",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/HolderLookup$RegistryLookup;getOrThrow(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/core/Holder$Reference;")
    )
    private static Holder.Reference<Enchantment> handleMissingLooting(HolderLookup.RegistryLookup<Enchantment> instance, ResourceKey<Enchantment> registryKey, Operation<Holder.Reference<Enchantment>> original) {
        return instance.get(registryKey).orElse(instance.getOrThrow(ModEnchantments.EMPTY_KEY));
    }
}
