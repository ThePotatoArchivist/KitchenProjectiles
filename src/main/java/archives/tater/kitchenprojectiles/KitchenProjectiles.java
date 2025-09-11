package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
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
import vectorwing.farmersdelight.common.tag.ModTags;

public class KitchenProjectiles implements ModInitializer {
	public static final String MOD_ID = "kitchenprojectiles";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static final int MIN_USE_DURATION = 6;

    private static <T extends Entity> EntityType<T> register(Identifier id, EntityType.Builder<T> type) {
        var key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id);
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

	public static final EntityType<KnifeEntity> KNIFE_ENTITY = register(
			id("knife"),
			EntityType.Builder.<KnifeEntity>create(KnifeEntity::new, SpawnGroup.MISC)
					.dimensions(0.4f, 0.4f)
					.maxTrackingRange(4)
					.trackingTickInterval(20)
	);

	public static final RegistryKey<DamageType> KNIFE_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("knife"));

    public static final TagKey<Item> LIGHT_KNIVES = TagKey.of(RegistryKeys.ITEM, id("light_knives"));

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
