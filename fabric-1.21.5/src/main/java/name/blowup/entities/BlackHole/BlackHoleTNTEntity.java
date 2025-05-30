package name.blowup.entities.BlackHole;

import name.blowup.entities.CustomTNTEntity;
import name.blowup.registering.ModBlocks;
import name.blowup.utils.ExplosionUtil;
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
     * @param tntCenter The tntCenter position of the explosion.
     */
    @Override
    protected void explode(ServerWorld world, Vec3d tntCenter) {
        ExplosionUtil.doBlackHoleExplosion(world, tntCenter);
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
