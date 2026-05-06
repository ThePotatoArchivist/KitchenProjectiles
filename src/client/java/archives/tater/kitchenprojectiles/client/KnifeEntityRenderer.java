package archives.tater.kitchenprojectiles.client;

import archives.tater.kitchenprojectiles.ThrownKnife;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;

public class KnifeEntityRenderer extends EntityRenderer<ThrownKnife, KnifeEntityRenderer.KnifeEntityRenderState> {
    private final ItemModelResolver itemModelManager;
    private final float scale;
    private final boolean lit;

    public static boolean intangible = false;

    public KnifeEntityRenderer(EntityRendererProvider.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelResolver();
        this.scale = scale;
        this.lit = lit;
    }

    public KnifeEntityRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0f, false);
    }

    @Override
    protected int getBlockLightLevel(ThrownKnife entity, BlockPos pos) {
        return lit ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public KnifeEntityRenderState createRenderState() {
        return new KnifeEntityRenderState();
    }

    @Override
    public void extractRenderState(ThrownKnife entity, KnifeEntityRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);

        // see ProjectileEntityRenderer#updateRenderState
        state.xRot = entity.getXRot(tickProgress);
        state.yRot = entity.getYRot(tickProgress);
        state.shake = entity.shakeTime - tickProgress;

        state.intangible = entity.isIntangible();
        itemModelManager.updateForNonLiving(state.knifeRenderState, entity.getStackClient(), ItemDisplayContext.NONE, entity);
    }

    @Override
    public void submit(KnifeEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
//        if (state.age < 2 && dispatcher.camera.getFocusedEntity().squaredDistanceTo(state.pos) < MIN_DISTANCE) return;

        matrices.pushPose();
        matrices.scale(0.85f * scale, 0.85f * scale, 0.85f * scale);
        matrices.translate(0, -0.1f, 0);
        matrices.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(state.xRot + 90.0F));
        matrices.translate(scale * 0.2, scale * 0.1, 0);
        matrices.mulPose(Axis.ZP.rotationDegrees(-45));
        matrices.mulPose(Axis.XP.rotationDegrees(180));

        if (state.intangible)
            intangible = true;

        state.knifeRenderState.submit(matrices, queue, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        intangible = false;

        matrices.popPose();

        super.submit(state, matrices, queue, cameraState);
    }

    public static class KnifeEntityRenderState extends ArrowRenderState {
        public boolean intangible;
        public ItemStackRenderState knifeRenderState = new ItemStackRenderState();
    }
}
