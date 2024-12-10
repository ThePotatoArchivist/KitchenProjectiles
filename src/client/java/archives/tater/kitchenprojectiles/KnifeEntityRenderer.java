package archives.tater.kitchenprojectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class KnifeEntityRenderer extends EntityRenderer<KnifeEntity> {
    private static final float MIN_DISTANCE = 3.5f * 3.5f;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public KnifeEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
    }

    public KnifeEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0f, false);
    }

    @Override
    protected int getBlockLight(KnifeEntity entity, BlockPos pos) {
        return lit ? 15 : super.getBlockLight(entity, pos);
    }

    @Override
    public void render(KnifeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity.age >= 2 || !(dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < MIN_DISTANCE)) {
            matrices.push();
            matrices.scale(0.85f * scale, 0.85f * scale, 0.85f * scale);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + 90.0F));
            matrices.translate(scale * 0.2, scale * 0.1, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            itemRenderer.renderItem(
                    entity.getKnifeStack(), ModelTransformationMode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId()
            );
            matrices.pop();
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    @Override
    public Identifier getTexture(KnifeEntity entity) {
        //noinspection deprecation
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
