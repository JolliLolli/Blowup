package blowup.proxy;

import blowup.Blowup;
import blowup.entities.BlackHoleEntity;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.MinecraftForgeClient;
import blowup.client.renderers.*;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
    public static RenderManager renderManager;
    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // This is good for rendering, key-bindings, and other client-side only things.
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        Blowup.LOG.info("I am the ClientProxy");

    }

    @Override
    public void init(FMLInitializationEvent event) {
        // Register normal entity renderers using RenderingRegistry:
        RenderingRegistry.registerEntityRenderingHandler(BlackHoleEntity.class,
            new BlackHoleRenderer(Minecraft.getMinecraft().getRenderManager()));
        // Register other entity renderers similarly...
    }
}
