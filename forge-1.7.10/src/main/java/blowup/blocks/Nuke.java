package blowup.blocks;

import blowup.entities.ModEntities;
import blowup.entities.NukeEntity;
import blowup.entities.CustomTNTEntity;
import blowup.utils.Kaboom;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

/**
 * Represents a custom TNT block that creates a Nuke Entity when detonated.
 * It makes a big boom and flings blocks everywhere.
 * This class extends the CustomTNTBlock class to provide specific behavior for the nuke TNT.
 */
public class Nuke extends CustomTNTBlock {

    public Nuke() {
        super();
        this.setBlockName("Nuke");
        this.setBlockTextureName("blowup:nuke");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public static NukeEntity createNukeEntity(World world, int x, int y, int z) {
        NukeEntity nuke = new NukeEntity(world);
        nuke.setPosition(x + 0.5, y, z + 0.5);
        nuke.fuse = 60;
        Kaboom.giveTntHop(nuke);
        return nuke;
    }
}
