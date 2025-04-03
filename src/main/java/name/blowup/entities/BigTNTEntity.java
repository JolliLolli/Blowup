package name.blowup.entities;

import name.blowup.blocks.BigTNT;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BigTNTEntity extends TntEntity {
    public BigTNTEntity(EntityType<BigTNTEntity> type, World world) {
        super(type, world);
    }


    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && this.getFuse() <= 0) {
            // Instead of calling the private explode(), call your custom explosion logic.
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                // Assuming your custom block is registered as BigTNT and you want to call its explode() method.
                // You might want to get a center position from the entity's position.
                Vec3d center = this.getPos();
                // Cast the block to BigTNT if needed, or call a static method in a utility class.
                // For example, if you have a static method:
                // BigTNT.primeBigTnt(serverWorld, BlockPos.ofFloored(center), null);
                // Or call your custom explosion method:
                BigTNT.explode(serverWorld, center);
            }
            // Remove the entity after explosion.
            this.discard();
        }
    }
}
