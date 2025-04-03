package name.blowup.blocks;

import name.blowup.ExplosionUtil;
import name.blowup.entities.ModEntities;
import name.blowup.entities.NukeEntity;
import name.blowup.entities.CustomTNTEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Nuke extends CustomTNTBlock {

    public Nuke(Settings settings) {
        super(settings);
    }

    @Override
    public void explode(ServerWorld world, Vec3d center) {
        ExplosionUtil.doNukeExplosion(world, center);
    }


    @Override
    protected CustomTNTEntity createCustomTNTEntity(World world, BlockPos pos) {
        NukeEntity entity = new NukeEntity(ModEntities.NUKE_ENTITY, world);
        entity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        entity.setFuse(60); // Set the fuse time or make it configurable.
        return entity;
    }
}
