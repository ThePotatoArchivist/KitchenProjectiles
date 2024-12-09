package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KitchenProjectilesClient implements ClientModInitializer {
	public static final Identifier THROWING_PREDICATE = new Identifier(KitchenProjectiles.MOD_ID, "throwing");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(KitchenProjectiles.KNIFE_ENTITY, KnifeEntityRenderer::new);

		ModelPredicateProviderRegistry.register(THROWING_PREDICATE, (stack, world, entity, seed) ->
				entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
		);

		var knives = Stream.of(
				"flint",
				"iron",
				"diamond",
				"golden",
				"netherite"
		).collect(Collectors.toMap(
				prefix -> new ModelIdentifier("farmersdelight", prefix + "_knife", "inventory"),
				prefix -> new Identifier(KitchenProjectiles.MOD_ID, "item/" + prefix + "_knife_throwing")
		));

		ModelLoadingPlugin.register(context -> {
			context.addModels(knives.values());

			context.modifyModelBeforeBake().register((unbakedModel, context1) -> {
				if (context1.id().toString().contains("iron_knife")) {
					System.out.println();
				}
				for (var modelId : knives.keySet()) {
					if (!modelId.equals(context1.id())) continue;
					if (!(unbakedModel instanceof JsonUnbakedModel jsonUnbakedModel)) break;

					jsonUnbakedModel.getOverrides().add(new ModelOverride(
							knives.get(modelId),
							List.of(new ModelOverride.Condition(THROWING_PREDICATE, 1))));

					break;
				}
				return unbakedModel;
			});
		});
	}
}
