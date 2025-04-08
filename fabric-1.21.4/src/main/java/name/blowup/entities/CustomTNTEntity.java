package name.blowup.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Represents a custom TNT entity that can be used to create various types of explosions.
 * This class extends the TntEntity class to provide specific behavior for custom TNT entities.
 */
public abstract class CustomTNTEntity extends TntEntity {

    public CustomTNTEntity(EntityType<? extends CustomTNTEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient() && this.getFuse() <= 0) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                // Call the custom explosion behavior defined in the concrete subclass.
                explode(serverWorld, this.getPos());
            }
            this.discard();
        }
    }

    // Each custom TNT entity defines its own explosion behavior.
    protected abstract void explode(ServerWorld world, Vec3d center);
}
