package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class KitchenProjectilesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(KitchenProjectiles.KNIFE_ENTITY, KnifeEntityRenderer::new);
	}
}
