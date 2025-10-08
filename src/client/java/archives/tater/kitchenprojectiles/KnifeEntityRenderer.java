package archives.tater.kitchenprojectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class KnifeEntityRenderer extends EntityRenderer<KnifeEntity, KnifeEntityRenderer.KnifeEntityRenderState> {
    private static final float MIN_DISTANCE = 3.5f * 3.5f;
    private final ItemModelManager itemModelManager;
    private final float scale;
    private final boolean lit;

    public static boolean intangible = false;

    public KnifeEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelManager();
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
    public KnifeEntityRenderState createRenderState() {
        return new KnifeEntityRenderState();
    }

    @Override
    public void updateRenderState(KnifeEntity entity, KnifeEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);

        // see ProjectileEntityRenderer#updateRenderState
        state.pitch = entity.getLerpedPitch(tickProgress);
        state.yaw = entity.getLerpedYaw(tickProgress);
        state.shake = entity.shake - tickProgress;

        state.intangible = entity.isIntangible();
        itemModelManager.updateForNonLivingEntity(state.knifeRenderState, entity.getStackClient(), ItemDisplayContext.NONE, entity);
    }

    @Override
    public void render(KnifeEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
//        if (state.age < 2 && dispatcher.camera.getFocusedEntity().squaredDistanceTo(state.pos) < MIN_DISTANCE) return;

        matrices.push();
        matrices.scale(0.85f * scale, 0.85f * scale, 0.85f * scale);
        matrices.translate(0, -0.1f, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch + 90.0F));
        matrices.translate(scale * 0.2, scale * 0.1, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        if (state.intangible)
            intangible = true;

        state.knifeRenderState.render(matrices, queue, state.light, OverlayTexture.DEFAULT_UV, 0);

        intangible = false;

        matrices.pop();

        super.render(state, matrices, queue, cameraState);
    }

    public static class KnifeEntityRenderState extends ProjectileEntityRenderState {
        public boolean intangible;
        public ItemRenderState knifeRenderState = new ItemRenderState();
    }
}
