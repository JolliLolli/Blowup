package blowup.entities;

import net.minecraft.entity.item.EntityTNTPrimed;a
import net.minecraft.world.World;

/**
 * Represents a custom TNT entity that can be used to create various types of explosions.
 * This class extends the TntEntity class to provide specific behavior for custom TNT entities.
 */
public abstract class CustomTNTEntity extends EntityTNTPrimed {

    public CustomTNTEntity(World world) {
        super(world);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.isRemote && this.fuse <= 0) {
            // Call the custom explosion behavior defined in the concrete subclass.
            explode();
            this.setDead();
        }
    }

    // Each custom TNT entity defines its own explosion behavior.
    protected abstract void explode();
    protected abstract void fuse(int i);
}
