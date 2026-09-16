package archives.tater.kitchenprojectiles.mixin.client;

import archives.tater.kitchenprojectiles.KitchenProjectiles;
import archives.tater.kitchenprojectiles.client.KitchenProjectilesClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {
    @Inject(
            method = "submitArmWithItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V")
    )
    private void knifeTransform(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        if (playerState.avatarRenderState == null || !playerState.avatarRenderState.isUsingItem || playerState.avatarRenderState.useItemHand != hand || !itemStack.is(KitchenProjectiles.THROWABLE_KNIVES)) return;
        KitchenProjectilesClient.transformFirstPerson(poseStack, (hand == InteractionHand.MAIN_HAND) == (playerState.avatarRenderState.mainArm == HumanoidArm.RIGHT));
    }

}
