package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import vectorwing.farmersdelight.FarmersDelight;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KitchenProjectilesClient implements ClientModInitializer {
	public static final ResourceLocation THROWING_PREDICATE = KitchenProjectiles.id("throwing");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(KitchenProjectiles.KNIFE_ENTITY, KnifeEntityRenderer::new);

		ItemProperties.registerGeneric(THROWING_PREDICATE, (stack, world, entity, seed) ->
				entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
		);

		var knives = Stream.of(
				"flint",
				"iron",
				"diamond",
				"golden",
				"netherite"
		).collect(Collectors.toMap(
				prefix -> new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(FarmersDelight.MODID, prefix + "_knife"), "inventory"),
				prefix -> KitchenProjectiles.id("item/" + prefix + "_knife_throwing")
		));

		ModelLoadingPlugin.register(context -> {
			context.addModels(knives.values());

			context.modifyModelBeforeBake().register((unbakedModel, context1) -> {
				for (var modelId : knives.keySet()) {
					if (!modelId.equals(context1.topLevelId())) continue;
					if (!(unbakedModel instanceof BlockModel jsonUnbakedModel)) break;

					jsonUnbakedModel.getOverrides().add(new ItemOverride(
							knives.get(modelId),
							List.of(new ItemOverride.Predicate(THROWING_PREDICATE, 1))));

					break;
				}
				return unbakedModel;
			});
		});
	}
}
