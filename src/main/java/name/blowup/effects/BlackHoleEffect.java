package name.blowup.effects;

import name.blowup.utils.Kaboom;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class BlackHoleEffect {

    /**
     * Creates a gradual black hole explosion effect at the specified center point.
     * Blocks within the defined suckRadius are gradually converted into falling block entities
     * with a swirling animation.
     *
     * @param world      The ServerWorld where the effect occurs.
     * @param center     The center point of the black hole.
     * @param suckRadius The maximum radius of the black hole's effect.
     */
    public static void suckBlocksGradually(ServerWorld world, Vec3d center, int suckRadius) {
        int guaranteedRadius = suckRadius - 4;
        int guaranteedRadiusSq = guaranteedRadius * guaranteedRadius;

        BlockPos origin = BlockPos.ofFloored(center);
        List<BlockPos> absorptionPositions = new ArrayList<>();

        // Gradually iterate through blocks to collect those that pass the falloff chance.
        Kaboom.iterateThroughBlocksGradually(world, center, suckRadius, pos -> {
            double squaredDistance = origin.getSquaredDistance(pos);
            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.getHardness(world, pos) < 0) return;

            double diff = squaredDistance - guaranteedRadiusSq;
            double suckChance = squaredDistance <= guaranteedRadiusSq
                    ? 1.0
                    : 1.0 / (1.0 + diff * diff);
            if (world.random.nextDouble() < suckChance) {
                absorptionPositions.add(pos.toImmutable());
            }
        }, () -> {
            // When iteration is complete, shuffle positions and start gradual processing.
            Random random = new Random(world.random.nextInt());
            Collections.shuffle(absorptionPositions, random);
            Vec3d diskNormal = randomUnitVector(random);
            int blocksPerTick = 5;
            processBlocksGradually(world, center, absorptionPositions, diskNormal, blocksPerTick);
        }, 20); // Process 20 blocks per tick during collection.
    }

    /**
     * Processes a subset of blocks per tick. For each block, it creates a falling block entity
     * with a swirling velocity and then schedules the next tick if blocks remain.
     */
    private static void processBlocksGradually(ServerWorld world, Vec3d center, List<BlockPos> positions,
                                                 Vec3d diskNormal, int blocksPerTick) {
        // Create a consumer that processes a single block.
        Consumer<BlockPos> blockConsumer = pos -> {
            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.getHardness(world, pos) < 0) return;

            // Remove the block.
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            Vec3d blockCenter = Vec3d.ofCenter(pos);

            // Calculate the inward (radial) component.
            Vec3d radial = center.subtract(blockCenter);
            Vec3d inward = radial.normalize().multiply(0.1); // Tune inward speed as needed.

            // Project the radial vector onto the accretion disk plane.
            Vec3d radialInPlane = radial.subtract(diskNormal.multiply(radial.dotProduct(diskNormal)));
            if (radialInPlane.lengthSquared() < 1e-4) {
                // If the radial vector is nearly perpendicular to the disk, choose an arbitrary vector in the plane.
                radialInPlane = diskNormal.crossProduct(new Vec3d(1, 0, 0));
                if (radialInPlane.lengthSquared() < 1e-4) {
                    radialInPlane = diskNormal.crossProduct(new Vec3d(0, 1, 0));
                }
            }
            radialInPlane = radialInPlane.normalize();

            // Calculate a tangential vector perpendicular to the radial projection (in the disk plane).
            Vec3d tangential = diskNormal.crossProduct(radialInPlane).normalize();
            Vec3d swirl = tangential.multiply(0.1); // Tune swirl speed as needed.

            // Combine the inward and tangential components.
            Vec3d velocity = inward.add(swirl);

            // Spawn the falling block entity and set its velocity.
            FallingBlockEntity fb = FallingBlockEntity.spawnFromBlock(world, pos, state);
            fb.setVelocity(velocity);
            if (world.random.nextDouble() < 0.9) {
                fb.dropItem = false;
            }
            world.spawnEntity(fb);
        };

        // Process the positions gradually using our helper.
        Kaboom.processPositionsGradually(world, positions, blockConsumer, () -> {
            // Optional onComplete action when all blocks have been processed.
        }, blocksPerTick);
    }

    /**
     * Generates a random unit vector.
     */
    private static Vec3d randomUnitVector(Random random) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double z = random.nextDouble() * 2 - 1; // Range: [-1, 1]
        double r = Math.sqrt(1 - z * z);
        double x = r * Math.cos(theta);
        double y = r * Math.sin(theta);
        return new Vec3d(x, y, z);
    }
}
