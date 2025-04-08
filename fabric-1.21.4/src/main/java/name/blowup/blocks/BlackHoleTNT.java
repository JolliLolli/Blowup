package name.blowup.blocks;

import name.blowup.entities.CustomTNTEntity;
import name.blowup.entities.ModEntities;
import name.blowup.entities.BlackHoleTNTEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Represents a custom TNT block that creates a Black Hole TNT Entity when detonated.
 * It creates a black hole effect.
 * This class extends the CustomTNTBlock class to provide specific behavior for the black hole TNT.
 */
public class BlackHoleTNT extends CustomTNTBlock {

    public BlackHoleTNT(Settings settings) {
        super(settings);
    }

    @Override
    protected CustomTNTEntity createCustomTNTEntity(World world, BlockPos pos) {
        BlackHoleTNTEntity entity = new BlackHoleTNTEntity(ModEntities.BLACK_HOLE_TNT_ENTITY, world);
        entity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        entity.setFuse(60); // Set the fuse time or make it configurable.
        return entity;
    }
}
