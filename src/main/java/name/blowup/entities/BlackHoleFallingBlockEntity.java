package name.blowup.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import name.blowup.utils.BlackHoleUtils;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * BlackHoleFallingBlockEntity is a custom FallingBlockEntity used by the black hole.
 * <p>
 * It is spawned when blocks are absorbed by the black hole. This entity disables gravity
 * and, every tick, recalculates its velocity so that it is drawn toward the black hole with
 * a swirling (tangential) component. When it gets sufficiently close (or after a set time),
 * additional logic could discard the entity.
 */
public class BlackHoleFallingBlockEntity extends FallingBlockEntity {

    private final Vec3d blackHoleCenter;
    private final Vec3d diskNormal;
    private double inwardSpeed;
    private double swirlSpeed;
    private final BlockState state;
    private final World world = getWorld();

    public static final Set<UUID> DEBUG_UUIDS = new HashSet<>();

    /**
     * Constructs a BlackHoleFallingBlockEntity.
     *
     * @param world          The ServerWorld in which the entity exists.
     * @param pos            The block position where the entity spawns.
     * @param state          The block state being converted.
     * @param blackHoleCenter The center of the black hole (used for computing pull).
     * @param diskNormal     The normal vector defining the accretion disk plane.
     * @param inwardSpeed    Multiplier for the inward (radial) velocity.
     * @param swirlSpeed     Multiplier for the tangential (swirl) velocity.
     */
    public BlackHoleFallingBlockEntity(ServerWorld world, BlockPos pos, BlockState state,
                                       Vec3d blackHoleCenter, Vec3d diskNormal,
                                       double inwardSpeed, double swirlSpeed) {
        super(EntityType.FALLING_BLOCK, world);
        this.blackHoleCenter = blackHoleCenter;
        this.diskNormal = diskNormal;
        this.inwardSpeed = inwardSpeed;
        this.swirlSpeed = swirlSpeed;
        this.state = state;
        this.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = getX();
        this.prevY = getY();
        this.prevZ = getZ();
        this.setFallingBlockPos(pos);
        this.intersectionChecked = true;
        this.setNoGravity(true);
        this.noClip = true; // Prevent collisions with blocks
        System.out.println("Spawned entity at " + pos + " with state " + state);
    }

    /**
     * Updates the entity's velocity every tick so that it continuously gets drawn toward the black hole.
     * The new velocity is calculated based on the current position relative to the black hole center
     * and the desired inward and swirl multipliers.
     */
    @Override
    public void tick() {
        super.tick();
        // Run server-side only.
        if (!world.isClient && blackHoleCenter != null && diskNormal != null) {
            Vec3d currentPos = this.getPos();
            // Calculate a new velocity vector using our swirling math.
            Vec3d newVelocity = BlackHoleUtils.calcSwirlVelocity(
                    blackHoleCenter, currentPos, diskNormal, inwardSpeed, swirlSpeed
            );

            this.setVelocity(newVelocity);
            if (this.isOnGround()) {
                this.timeFalling = 0;
            }
            // Check if we've hit the event horizon (1 block radius)
            if (currentPos.squaredDistanceTo(blackHoleCenter) < 3.0) {
                System.out.println("Black hole entity " + this.getUuid() + " is being consumed.");
                this.discard(); // Entity is consumed by the black hole
            }

            // Debug output: log only if this entity's UUID is in our debug set.
            if (DEBUG_UUIDS.contains(this.getUuid())) {
                System.out.println("Debug Entity " + this.getUuid() + " at pos: "
                        + currentPos + " with velocity: " + newVelocity);
            }
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entry) {
        System.out.println("Creating spawn packet for BlackHoleEntity");
        return new EntitySpawnS2CPacket(this, entry);
    }

    public static BlackHoleFallingBlockEntity spawnBlackHoleBlock(ServerWorld world, BlockPos pos,
                                                                  BlockState state, Vec3d blackHoleCenter,
                                                                  Vec3d diskNormal, double inwardSpeed, double swirlSpeed) {
        System.out.println("Going to spawn entity");
        BlackHoleFallingBlockEntity entity = new BlackHoleFallingBlockEntity(world, pos, state,
                blackHoleCenter, diskNormal, inwardSpeed, swirlSpeed);
        entity.setUuid(UUID.randomUUID());

        if (Math.random() < 0.005) {
            System.out.println("Debugging entity spawned with UUID: " + entity.getUuid());
            BlackHoleFallingBlockEntity.DEBUG_UUIDS.add(entity.getUuid());
        }

        world.spawnEntity(entity);
        return entity;
    }



    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }
}
