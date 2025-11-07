package archives.tater.kitchenprojectiles;

import archives.tater.kitchenprojectiles.client.KnifeEntityRenderer;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import vectorwing.farmersdelight.FarmersDelight;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod(value = KitchenProjectiles.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = KitchenProjectiles.MOD_ID, value = Dist.CLIENT)
public class KitchenProjectilesClient {

    public static final ResourceLocation THROWING_PREDICATE = KitchenProjectiles.id("throwing");

    public KitchenProjectilesClient(ModContainer container) {
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(KitchenProjectiles.KNIFE_ENTITY.get(), KnifeEntityRenderer::new);
    }

    @SubscribeEvent
    private static void onClientSetup(FMLClientSetupEvent event) {
        ItemProperties.registerGeneric(THROWING_PREDICATE, (stack, level, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
        );
    }

    // Needs to run earlier than the client setup event, otherwise it won't make it in time for the first model loading
    @SubscribeEvent
    private static void commonSetup(FMLCommonSetupEvent event) {
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
