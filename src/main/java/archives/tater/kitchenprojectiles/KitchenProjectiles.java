package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;

import net.minecraft.block.Block;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.List;

public class KitchenProjectiles implements ModInitializer {
	public static final String MOD_ID = "kitchenprojectiles";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static Identifier fdId(String path) {
        return Identifier.of(FarmersDelight.MODID, path);
    }

	public static final EntityType<KnifeEntity> KNIFE_ENTITY = Registry.register(
			Registries.ENTITY_TYPE,
			id("knife"),
			EntityType.Builder.<KnifeEntity>create(KnifeEntity::new, SpawnGroup.MISC)
					.dimensions(0.4f, 0.4f)
					.maxTrackingRange(4)
					.trackingTickInterval(20)
					.build()
	);

	public static final RegistryKey<DamageType> KNIFE_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("knife"));

    public static final TagKey<Item> LIGHT_KNIVES = TagKey.of(RegistryKeys.ITEM, id("light_knives"));

    public static final LazyRegistryRef<Item> IRON_KNIFE = new LazyRegistryRef<>(Registries.ITEM, fdId("iron_knife"));
    public static final LazyRegistryRef<Block> CUTTING_BOARD = new LazyRegistryRef<>(Registries.BLOCK, fdId("cutting_board"));
    public static final LazyRegistryRef<ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>>> BACKSTABBING = new LazyRegistryRef<>(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, fdId("backstabbing"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		KitchenProjectilesSounds.init();

        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, enchantingContext) ->
            target.isIn(ModTags.KNIFE_ENCHANTABLE) &&
                    enchantment.getKey().map(key -> key == Enchantments.LOYALTY || key == Enchantments.MULTISHOT).orElse(false)
                    ? TriState.TRUE
                    : TriState.DEFAULT
        );
	}
}
