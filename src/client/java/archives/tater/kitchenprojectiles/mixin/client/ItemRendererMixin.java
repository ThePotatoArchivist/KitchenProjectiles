package archives.tater.kitchenprojectiles.mixin.client;

import archives.tater.kitchenprojectiles.KnifeEntityRenderer;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyArg(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V"),
            index = 5
    )
    private VertexConsumer intangibleTranslucent(VertexConsumer vertices, @Local(argsOnly = true) VertexConsumerProvider vertexConsumers) {
        if (!KnifeEntityRenderer.intangible)
            return vertices;
        return vertexConsumers.getBuffer(TexturedRenderLayers.getItemEntityTranslucentCull());
    }

    @ModifyArg(
            method = "renderBakedItemQuads",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V"),
            index = 5
    )
    private float intangibleTranslucent(float alpha) {
        if (!KnifeEntityRenderer.intangible)
            return alpha;
        return 0.5f;
    }
}
