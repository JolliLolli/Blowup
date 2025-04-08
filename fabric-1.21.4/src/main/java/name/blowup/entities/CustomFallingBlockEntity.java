package name.blowup.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * A base class for custom falling block entities.
 * This class provides common functionality such as basic gravity,
 * collision handling, and tracking of falling time.
 */
public abstract class CustomFallingBlockEntity extends FallingBlockEntity {
    // Flag to determine if this entity should obey gravity.
    protected boolean obeyGravity = true;
    // Flag for whether collision handling is enabled.
    protected boolean enableCollision = true;

    public CustomFallingBlockEntity(EntityType<? extends FallingBlockEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        // Apply gravity if enabled (vanilla gravity is ~0.04 per tick)
        if (obeyGravity) {
            Vec3d currentVelocity = this.getVelocity();
            // You might adjust this gravity value or add drag, etc.
            this.setVelocity(currentVelocity.add(0, -0.04, 0));
        }
        
        // Update position based on velocity.
        this.setPos(this.getX() + this.getVelocity().x,
                    this.getY() + this.getVelocity().y,
                    this.getZ() + this.getVelocity().z);
        
        // Update previous position fields for interpolation.
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        
        // Update falling time (if the entity is on the ground, reset it).
        if (this.isOnGround()) {
            this.timeFalling = 0;
        } else {
            this.timeFalling++;
        }
        
        // Note: This base tick does not call super.tick() so that
        // vanilla collision and landing logic can be selectively enabled
        // via the flags (or overridden completely in subclasses).
    }
}
