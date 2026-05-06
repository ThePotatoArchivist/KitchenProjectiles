package archives.tater.kitchenprojectiles.mixin.client;

import archives.tater.kitchenprojectiles.KitchenProjectiles;
import archives.tater.kitchenprojectiles.client.KitchenProjectilesClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL")
    )
    private void extractThrowingKnife(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        state.setData(KitchenProjectilesClient.THROWING_KNIFE, entity.isUsingItem() && entity.getUseItem().is(KitchenProjectiles.THROWABLE_KNIVES));
    }
}
