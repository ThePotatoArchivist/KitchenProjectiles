package archives.tater.kitchenprojectiles.mixin.client;

import archives.tater.kitchenprojectiles.client.KnifeEntityRenderer;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyArg(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"),
            index = 5
    )
    private VertexConsumer intangibleTranslucent(VertexConsumer vertices, @Local(argsOnly = true) MultiBufferSource vertexConsumers) {
        if (!KnifeEntityRenderer.intangible)
            return vertices;
        return vertexConsumers.getBuffer(Sheets.translucentItemSheet());
    }

    @ModifyArg(
            method = "renderQuadList",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFIIZ)V"),
            index = 5
    )
    private float intangibleTranslucent(float alpha) {
        if (!KnifeEntityRenderer.intangible)
            return alpha;
        return 0.5f;
    }
}
