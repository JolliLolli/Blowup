package name.blowup.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import name.blowup.utils.BlackHoleUtils;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Falling block entity pulled by a black hole.
 */
public class BlackHoleFallingBlockEntity extends CustomFallingBlockEntity {
    private Vec3d blackHoleCenter;
    private Vec3d diskNormal;
    private double inwardSpeed;
    private double swirlSpeed;
    private BlockState storedState;
    private float spinOffset;
    private Vector3f spinAxis;
    private static final TrackedData<Integer> BLOCK_STATE = DataTracker.registerData(BlackHoleFallingBlockEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // Constructor for the entity type registration.
    public BlackHoleFallingBlockEntity(EntityType<? extends FallingBlockEntity> type, World world) {
        super(type, world);
        // Start tracking the block state; default to air.
        this.obeyGravity = false;
        this.enableCollision = false;
        this.getDataTracker().set(BLOCK_STATE, Block.getRawIdFromState(Blocks.AIR.getDefaultState()));
        this.storedState = Blocks.AIR.getDefaultState();
        this.blackHoleCenter = Vec3d.ZERO;
        this.setNoGravity(true);
        this.noClip = true;
    }

    // Constructor for spawning the entity in the world.
    public BlackHoleFallingBlockEntity(ServerWorld world,
                                       BlockPos pos,
                                       BlockState state,
                                       Vec3d blackHoleCenter,
                                       Vec3d diskNormal,
                                       double inwardSpeed,
                                       double swirlSpeed) {
        this(ModEntities.BLACK_HOLE_FALLING_BLOCK_ENTITY, world);
        this.blackHoleCenter = blackHoleCenter;
        this.diskNormal = diskNormal;
        this.inwardSpeed = inwardSpeed;
        this.swirlSpeed = swirlSpeed;
        this.storedState = state;
        this.spinOffset = this.random.nextFloat() * (float)(2 * Math.PI); // radians

        this.spinAxis = new Vector3f(
                random.nextFloat() * 2 - 1,
                random.nextFloat() * 2 - 1,
                random.nextFloat() * 2 - 1
        ).normalize();

        // Update the tracked data with the correct state.
        this.dataTracker.set(BLOCK_STATE, Block.getRawIdFromState(state));

        //DEBUG_UUIDS.add(this.getUuid());

        this.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        this.setFallingBlockPos(pos);
        this.intersectionChecked = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BLOCK_STATE, Block.getRawIdFromState(Blocks.AIR.getDefaultState()));
    }

    /**
     * Override tick() to implement custom swirling behavior for the black hole effect.
     * We deliberately do not call super.tick() here because we don't want vanilla landing/collision logic.
     */
    @Override
    public void tick() {
        // Run custom black-hole swirl logic on the server.
        if (!this.getWorld().isClient && blackHoleCenter != null && diskNormal != null) {
            Vec3d currentPos = this.getPos();
            // Calculate a new velocity vector using our swirling math.
            Vec3d newVelocity = BlackHoleUtils.calcSwirlVelocity(
                blackHoleCenter, currentPos, diskNormal, inwardSpeed, swirlSpeed
            );

            this.setVelocity(newVelocity);

            // Check if we've hit the event horizon (1 block radius)
            if (currentPos.squaredDistanceTo(blackHoleCenter) < 3.0) {
                this.discard(); // Entity is consumed by the black hole
            }
        }

        // Manually update the entity's position based on its current velocity.
        this.setPos(this.getX() + this.getVelocity().x,
                    this.getY() + this.getVelocity().y,
                    this.getZ() + this.getVelocity().z);

        // Update previous position fields.
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();

        // We deliberately do NOT call super.tick() so we bypass vanilla collision and landing logic.
    }

    @Override
    public BlockState getBlockState() {
        // Use the data tracker to determine what block state to render.
        int rawState = this.dataTracker.get(BLOCK_STATE);
        return Block.getStateFromRawId(rawState);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entry) {
        return new EntitySpawnS2CPacket(this, entry);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.storedState = Block.getStateFromRawId(packet.getEntityData());
    }
}
