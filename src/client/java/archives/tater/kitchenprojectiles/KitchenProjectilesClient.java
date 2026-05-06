package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class KitchenProjectilesClient implements ClientModInitializer {

	public static final RenderStateDataKey<Boolean> THROWING_KNIFE = RenderStateDataKey.create(() -> KitchenProjectiles.MOD_ID + ":throwing_knife");

	public static void transformFirstPerson(PoseStack poseStack, boolean rightArm) {
		poseStack.translate(rightArm ? 0.3 : -0.3, 0, -0.1);
		poseStack.mulPose(Axis.XP.rotationDegrees(-45));
	}

    public static void transformThirdPerson(PoseStack poseStack) {
		poseStack.translate(0, -0.3, 0.15);
		poseStack.mulPose(Axis.XP.rotationDegrees(-160));
    }

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRenderers.register(KitchenProjectiles.KNIFE_ENTITY, KnifeEntityRenderer::new);
	}
}
