package name.blowup.blocks;

import name.blowup.entities.CustomTNTEntity;
import name.blowup.entities.NukeEntity;
import name.blowup.registering.ModEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Represents a custom TNT block that creates a Nuke Entity when detonated.
 * It makes a big boom and flings blocks everywhere.
 * This class extends the CustomTNTBlock class to provide specific behavior for the nuke TNT.
 */
public class Nuke extends CustomTNTBlock {

    public Nuke(Settings settings) {
        super(settings);
    }

    @Override
    protected CustomTNTEntity createCustomTNTEntity(World world, BlockPos pos) {
        NukeEntity entity = new NukeEntity(ModEntities.NUKE_ENTITY, world);
        entity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        entity.setFuse(60); // Set the fuse time or make it configurable.
        return entity;
    }
}
