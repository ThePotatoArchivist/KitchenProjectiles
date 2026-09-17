package archives.tater.kitchenprojectiles.mixin.enchancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;

import moriyashiine.enchancement.common.Enchancement;
import moriyashiine.enchancement.common.init.EnchancementEnchantments;

@Mixin(LootItemRandomChanceWithEnchantedBonusCondition.class)
public class RandomChanceWithEnchantedBonusLootConditionMixin {
    @WrapOperation(
            method = "lambda$randomChanceAndLootingBoost$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/HolderGetter;getOrThrow(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/core/Holder$Reference;")
    )
    private static Holder.Reference<Enchantment> handleMissingLooting(HolderGetter<Enchantment> instance, ResourceKey<Enchantment> id, Operation<Holder.Reference<Enchancement>> original) {
        return instance.get(id).orElse(instance.getOrThrow(EnchancementEnchantments.EMPTY_KEY));
    }
}
