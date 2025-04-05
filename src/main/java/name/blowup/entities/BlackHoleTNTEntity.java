package name.blowup.entities;

import name.blowup.utils.ExplosionUtil;
import name.blowup.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlackHoleTNTEntity extends CustomTNTEntity {

    public BlackHoleTNTEntity(EntityType<? extends CustomTNTEntity> type, World world) {
        super(type, world);
    }

    /**
     * This method is called when the TNT block is triggered to explode.
     * It will create a custom explosion effect.
     *
     * @param world The world where the explosion occurs.
     * @param center The center position of the explosion.
     */
    @Override
    protected void explode(ServerWorld world, Vec3d center) {
        ExplosionUtil.doBlackHoleExplosion(world, center);
    }

    /**
     * This method returns the block state of the custom TNT block.
     * In particular, it retrieves the texture of the block and uses it for the entity
     *
     * @return The block state of the custom TNT block.
     */
    @Override
    public BlockState getBlockState() {
        return ModBlocks.BLACK_HOLE_TNT.getDefaultState();
    }
}
