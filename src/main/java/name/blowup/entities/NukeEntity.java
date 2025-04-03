package name.blowup.entities;

import name.blowup.ExplosionUtil;
import name.blowup.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NukeEntity extends CustomTNTEntity {

    public NukeEntity(EntityType<? extends CustomTNTEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void explode(ServerWorld world, Vec3d center) {
        ExplosionUtil.doNukeExplosion(world, center);
    }

    @Override
    public BlockState getBlockState() {
        return ModBlocks.NUKE.getDefaultState();
    }
}
