package name.blowup;

import name.blowup.client.guis.DetonatorScreen;
import name.blowup.client.renderer.DetonatorRenderer;
import name.blowup.entities.ModBlockEntities;
import name.blowup.entities.ModEntities;
import name.blowup.guis.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.TntEntityRenderer;
import name.blowup.client.renderer.BlackHoleFallingBlockRenderer;
import name.blowup.client.renderer.BlackHoleRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static name.blowup.Blowup.MOD_ID;

@SuppressWarnings("unused")
public class BlowupClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// Register the renderer for your custom TNT entity.
		// Using the vanilla TntEntityRenderer for simplicity.
		EntityRendererRegistry.register(ModEntities.NUKE_ENTITY, TntEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLACK_HOLE_TNT_ENTITY, TntEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLACK_HOLE_ENTITY, BlackHoleRenderer::new);
		EntityRendererRegistry.register(ModEntities.BLACK_HOLE_FALLING_BLOCK_ENTITY, BlackHoleFallingBlockRenderer::new);
		BlockEntityRendererFactories.register(ModBlockEntities.DETONATOR_ENTITY, ctx -> new DetonatorRenderer());
		HandledScreens.register(ModScreenHandlers.DETONATOR, DetonatorScreen::new);
	}
}
