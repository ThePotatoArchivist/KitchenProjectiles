package archives.tater.kitchenprojectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class KnifeEntityRenderer extends EntityRenderer<KnifeEntity> {
    private static final float MIN_DISTANCE = 3.5f * 3.5f;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public static boolean intangible = false;

    public KnifeEntityRenderer(EntityRendererProvider.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
    }

    public KnifeEntityRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0f, false);
    }

    @Override
    protected int getBlockLightLevel(KnifeEntity entity, BlockPos pos) {
        return lit ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(KnifeEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (entity.tickCount < 2 && entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < MIN_DISTANCE) return;

        matrices.pushPose();
        matrices.scale(0.85f * scale, 0.85f * scale, 0.85f * scale);
        matrices.translate(0, -0.1f, 0);
        matrices.mulPose(Axis.YP.rotationDegrees(Mth.lerp(tickDelta, entity.yRotO, entity.getYRot()) - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(tickDelta, entity.xRotO, entity.getXRot()) + 90.0F));
        matrices.translate(scale * 0.2, scale * 0.1, 0);
        matrices.mulPose(Axis.ZP.rotationDegrees(-45));
        matrices.mulPose(Axis.XP.rotationDegrees(180));

        if (entity.isIntangible())
            intangible = true;

        itemRenderer.renderStatic(
                entity.getStackClient(), ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, entity.level(), entity.getId()
        );

        intangible = false;

        matrices.popPose();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ResourceLocation getTextureLocation(KnifeEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
