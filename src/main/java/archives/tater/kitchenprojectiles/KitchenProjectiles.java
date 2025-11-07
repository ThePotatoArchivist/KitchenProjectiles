package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vectorwing.farmersdelight.common.tag.ModTags;

public class KitchenProjectiles implements ModInitializer {
	public static final String MOD_ID = "kitchenprojectiles";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

	public static final EntityType<KnifeEntity> KNIFE_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			id("knife"),
			EntityType.Builder.<KnifeEntity>of(KnifeEntity::new, MobCategory.MISC)
					.sized(0.4f, 0.4f)
					.clientTrackingRange(4)
					.updateInterval(20)
					.build()
	);

	public static final ResourceKey<DamageType> KNIFE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, id("knife"));

    public static final TagKey<Item> LIGHT_KNIVES = TagKey.create(Registries.ITEM, id("light_knives"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		KitchenProjectilesSounds.init();

        EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, enchantingContext) ->
            target.is(ModTags.KNIFE_ENCHANTABLE) &&
                    enchantment.unwrapKey().map(key -> key == Enchantments.LOYALTY || key == Enchantments.MULTISHOT).orElse(false)
                    ? TriState.TRUE
                    : TriState.DEFAULT
        );
	}
}
