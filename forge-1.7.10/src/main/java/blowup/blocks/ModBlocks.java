package blowup.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks {
    public static void init() {
        GameRegistry.registerBlock(new Nuke(), "nuke");
    }
}
