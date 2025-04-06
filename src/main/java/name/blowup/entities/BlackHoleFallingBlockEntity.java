package name.blowup.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
 * Falling block entity pulled by a black hole.
 */
public class BlackHoleFallingBlockEntity extends FallingBlockEntity {

    // We'll remove 'final' so these can be set differently by the minimal vs. advanced constructors.
    private Vec3d blackHoleCenter;
    private Vec3d diskNormal;
    private double inwardSpeed;
    private double swirlSpeed;
    private BlockState storedState;
    private static final TrackedData<Integer> BLOCK_STATE = DataTracker.registerData(BlackHoleFallingBlockEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final Set<UUID> DEBUG_UUIDS = new HashSet<>();

    // Constructor for the entity type registration.
    public BlackHoleFallingBlockEntity(EntityType<? extends FallingBlockEntity> type, World world) {
        super(type, world);
        // Start tracking the block state; default to air.
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
        // Update the tracked data with the correct state.
        this.dataTracker.set(BLOCK_STATE, Block.getRawIdFromState(state));

        if (random.nextDouble() < 0.005) {
            DEBUG_UUIDS.add(this.getUuid());
        }

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

    @Override
    public void tick() {
        super.tick();

        // Only do black-hole swirl logic if we're on the server and we have a valid center/disk.
        if (!this.getWorld().isClient && blackHoleCenter != null && diskNormal != null) {
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
                if (DEBUG_UUIDS.contains(this.getUuid())) {
                    System.out.println("Destroying entity " + this.getUuid() + " at pos: " + currentPos);
                }
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
    public BlockState getBlockState() {
        // Use the data tracker to determine what block state to render.
        int rawState = this.dataTracker.get(BLOCK_STATE);
        BlockState trackedState = Block.getStateFromRawId(rawState);
        System.out.println("Block state rendering as: " + trackedState);
        return trackedState;
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

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("BlockState", Block.getRawIdFromState(storedState));
        nbt.putDouble("CenterX", blackHoleCenter.x);
        nbt.putDouble("CenterY", blackHoleCenter.y);
        nbt.putDouble("CenterZ", blackHoleCenter.z);
        System.out.println("Writing NBT: " + nbt);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        storedState = Block.getStateFromRawId(nbt.getInt("BlockState"));
        blackHoleCenter = new Vec3d(
                nbt.getDouble("CenterX"),
                nbt.getDouble("CenterY"),
                nbt.getDouble("CenterZ")
        );
        System.out.println("Reading NBT: " + nbt);
    }

}
