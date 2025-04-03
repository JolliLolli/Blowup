package name.blowup;

import name.blowup.entities.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.TntEntityRenderer;

public class BlowupClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Register the renderer for your custom TNT entity.
		// Using the vanilla TntEntityRenderer for simplicity.
		EntityRendererRegistry.register(ModEntities.NUKE_ENTITY, TntEntityRenderer::new);
	}
}
