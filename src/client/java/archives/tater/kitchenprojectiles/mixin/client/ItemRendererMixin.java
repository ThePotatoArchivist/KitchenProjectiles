//package archives.tater.kitchenprojectiles.mixin.client;
//
//import archives.tater.kitchenprojectiles.client.KnifeEntityRenderer;
//import com.llamalad7.mixinextras.sugar.Local;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.Sheets;
//import net.minecraft.client.renderer.entity.ItemRenderer;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//
//@Mixin(ItemRenderer.class)
//public class ItemRendererMixin {
//    @ModifyArg(
//            method = "renderItem",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderQuadList(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Ljava/util/List;[III)V"),
//            index = 1
//    )
//    private static VertexConsumer intangibleTranslucent(VertexConsumer vertices, @Local(argsOnly = true) MultiBufferSource vertexConsumers) {
//        if (!KnifeEntityRenderer.intangible)
//            return vertices;
//        return vertexConsumers.getBuffer(Sheets.translucentItemSheet());
//    }
//
//    @ModifyArg(
//            method = "renderQuadList",
//            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFII)V"),
//            index = 5
//    )
//    private static float intangibleTranslucent(float alpha) {
//        if (!KnifeEntityRenderer.intangible)
//            return alpha;
//        return 0.5f;
//    }
//}
