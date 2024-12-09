package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KitchenProjectiles implements ModInitializer {
	public static final String MOD_ID = "kitchenprojectiles";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<KnifeEntity> KNIFE_ENTITY = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier(MOD_ID, "knife"),
			FabricEntityTypeBuilder.<KnifeEntity>create(SpawnGroup.MISC, KnifeEntity::new)
					.dimensions(EntityDimensions.fixed(0.4f, 0.4f))
					.trackRangeChunks(4)
					.trackedUpdateRate(20)
					.build()
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		
	}
}
