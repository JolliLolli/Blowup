package name.blowup;

import name.blowup.client.guis.DetonatorScreen;
import name.blowup.client.renderer.BlackHole.BlackHoleFallingBlockRenderer;
import name.blowup.client.renderer.BlackHole.BlackHoleRenderer;
import name.blowup.client.renderer.DetonatorRenderer;
import name.blowup.registering.ModBlockEntities;
import name.blowup.registering.ModEntities;
import name.blowup.registering.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.TntEntityRenderer;

public class BlowupClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(ModEntities.NUKE_ENTITY, TntEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLACK_HOLE_TNT_ENTITY, TntEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLACK_HOLE_ENTITY, BlackHoleRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLACK_HOLE_FALLING_BLOCK_ENTITY, BlackHoleFallingBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.DETONATOR_ENTITY, ctx -> new DetonatorRenderer());
		HandledScreens.register(ModScreenHandlers.DETONATOR, DetonatorScreen::new);
	}
}