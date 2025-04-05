package name.blowup.entities;

import name.blowup.utils.BlackHoleUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * BlackHoleEntity represents a black hole with an animated absorption effect.
 * <p>
 * It handles its own lifecycle (growing, holding, and shrinking) and delegates
 * the heavy lifting of block absorption to BlackHoleUtils. Customize parameters such
 * as scale and timings in the tick() method.
 */
public class BlackHoleEntity extends Entity {

    private float scale;
    // For absorption effect:
    private List<BlockPos> absorptionPositions = null;
    private Vec3d diskNormal = null;
    private World world = getWorld();

    public BlackHoleEntity(EntityType<?> type, World world) {
        super(type, world);
        System.out.println("Spawning black hole at");
        this.scale = 0.0F; // Initial scale
    }

//    /**
//     * Constructor for creating a black hole entity at a specific position.
//     * You can change the scale values and timing constants for different effects.
//     */
//    @Override
//    public void tick() {
//        super.tick();
//
//        // Lifetime constants
//        int totalLife = 200;       // 10 seconds @ 20 TPS
//        int growDuration = 40;     // ticks 0-39: growth
//        int holdDuration = 140;    // ticks 40-179: hold full size
//        int shrinkDuration = 20;   // ticks 180-199: shrink
//        float maxScale = 5.0f;
//
//        // Update the age of the entity
//        if (this.age < growDuration) {
//            // Ease-in-out grow using a smoothstep curve
//            float progress = (float) age / growDuration;
//            this.scale = smoothstep(0.0f, maxScale, progress);
//        } else if (this.age < growDuration + holdDuration) {
//            this.scale = maxScale;
//        } else if (this.age < totalLife) {
//            // Ease-out shrink
//            float progress = (float)(this.age - growDuration - holdDuration) / shrinkDuration;
//            this.scale = smoothstep(maxScale, 0.0f, progress);
//        } else {
//            this.discard();
//        }
//
//        // Start the absorption effect at tick 20 and end it at tick 180
//        // Uses collectAbsorptionPositions to gather blocks
//        // and processAbsorptionBlocks to handle them.
//        if (this.age >= 20 && this.age < 180) {
//            if (absorptionPositions == null) {
//                int suckRadius = 25;
//                absorptionPositions = BlackHoleUtils.collectAbsorptionPositions((ServerWorld) world, this.getPos(), suckRadius);
//                // Shuffle positions using a seeded Random instance.
//                Random random = new Random(world.random.nextInt());
//                Collections.shuffle(absorptionPositions, random);
//                diskNormal = BlackHoleUtils.randomUnitVector(random);
//            }
//
//            // Safely process a few blocks PER TICK — no recursion!
//            BlackHoleUtils.processAbsorptionBatch(
//                (ServerWorld) world,
//                getPos(),
//                absorptionPositions,
//                diskNormal,
//                5,      // blocks per tick
//                0.1,    // inward speed
//                0.1     // swirl speed
//            );
//        }
//    }

    @Override
    public void tick() {
        super.tick();
        // Lifetime constants
        int totalLife = 200;       // 10 seconds @ 20 TPS
        int growDuration = 40;     // ticks 0-39: growth
        int holdDuration = 140;    // ticks 40-179: hold full size
        int shrinkDuration = 20;   // ticks 180-199: shrink
        float maxScale = 5.0f;

        // Update the age of the entity
        if (this.age < growDuration) {
            // Ease-in-out grow using a smoothstep curve
            float progress = (float) age / growDuration;
            this.scale = smoothstep(0.0f, maxScale, progress);
        } else if (this.age < growDuration + holdDuration) {
            this.scale = maxScale;
        } else if (this.age < totalLife) {
            // Ease-out shrink
            float progress = (float)(this.age - growDuration - holdDuration) / shrinkDuration;
            this.scale = smoothstep(maxScale, 0.0f, progress);
        } else {
            this.discard();
        }

        if (this.age >= 20 && this.age < 180) {
            if (absorptionPositions == null) {
                absorptionPositions = BlackHoleUtils.collectAbsorptionPositions((ServerWorld) world, getPos(), 10);
                Random random = new Random(world.random.nextInt());
                Collections.shuffle(absorptionPositions, random);
                diskNormal = BlackHoleUtils.randomUnitVector(random);
            }
            // Process a batch of blocks each tick
            int blocksPerTick = 5;
            BlackHoleUtils.processAbsorptionBatch((ServerWorld) world, getPos(), absorptionPositions, diskNormal, blocksPerTick, 0.1, 0.1);
        }
    }

    private float smoothstep(float from, float to, float t) {
        t = Math.max(0, Math.min(1, t)); // clamp
        t = t * t * (3 - 2 * t); // smoothstep easing
        return from + (to - from) * t;
    }

    @Override
    public boolean isImmuneToExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entry) {
        System.out.println("Creating spawn packet for BlackHoleEntity");
        return new EntitySpawnS2CPacket(this, entry);
    }

    // Getter for the current scale, to be used in the renderer
    public float getScale() {
        return this.scale;
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
