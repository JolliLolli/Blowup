package blowup.entities;

import blowup.utils.ExplosionUtil;
import blowup.blocks.ModBlocks;
import net.minecraft.block.BlockVine;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import static net.minecraft.util.Vec3.createVectorHelper;

/**
 * Represents a custom TNT entity that creates a big explosion through doNukeExplosion().
 * This class extends the CustomTNTEntity class to provide specific behavior for the nuke TNT.
 */
public class NukeEntity extends CustomTNTEntity {

    private final Vec3 position;
    public NukeEntity(World world) {
        super(world);
        this.fuse = 60;
        this.position = createVectorHelper(this.posX, this.posY, this.posZ);
    }

    @Override
    protected void explode() {
        // Use your ExplosionUtil helper.
        // For example, if your util expects raw coordinates:
        ExplosionUtil.doNukeExplosion(this.worldObj, this.position);
    }
}
