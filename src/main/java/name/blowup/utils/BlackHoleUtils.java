package name.blowup.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BlackHoleUtils contains utility methods and helper functions for implementing the
 * black hole's block absorption and swirling animations.
 * <p>
 * Methods include:
 * <ul>
 *     <li>{@link #collectAbsorptionPositions(ServerWorld, Vec3d, int)} – Collects block positions within a sphere using a falloff chance.</li>
 *     <li>{@link #processAbsorptionBlocks(ServerWorld, Vec3d, List, Vec3d, int, double, double)} – Processes blocks gradually by spawning falling block entities with swirling velocity.</li>
 *     <li>General helper methods such as {@link #randomUnitVector(Random)} and gradual iteration helpers.</li>
 * </ul>
 */
public class BlackHoleUtils {

    /**
     * Iterates over blocks within a sphere of given radius and collects those that pass
     * the absorption chance check. Blocks inside (suckRadius - 4) are guaranteed, and those
     * near the edge have a steep falloff chance.
     *
     * @param world     The ServerWorld instance.
     * @param center    The center point of the effect.
     * @param suckRadius The maximum radius of the effect.
     * @return A list of block positions to be absorbed.
     */
    public static List<BlockPos> collectAbsorptionPositions(ServerWorld world, Vec3d center, int suckRadius) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos origin = BlockPos.ofFloored(center);
        int guaranteedRadius = suckRadius - 4;
        int guaranteedRadiusSq = guaranteedRadius * guaranteedRadius;

        Kaboom.iterateThroughBlocks(center, suckRadius, pos -> {
            double squaredDistance = origin.getSquaredDistance(pos);
            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.getHardness(world, pos) < 0) return;

            double suckChance = squaredDistance <= guaranteedRadiusSq
                    ? 1.0
                    : 1.0 / (1.0 + Math.pow(squaredDistance - guaranteedRadiusSq, 2));

            if (world.random.nextDouble() < suckChance) {
                positions.add(pos.toImmutable());
            }
        });
        return positions;
    }

    /**
     * Processes a fixed number of blocks (a batch) from the list.
     * This method is intended to be called once per tick from your entity,
     * so no recursion is needed.
     *
     * @param world         The ServerWorld instance.
     * @param center        The center of the effect.
     * @param positions     The list of block positions to process.
     * @param diskNormal    The normal vector defining the accretion disk plane.
     * @param blocksPerTick Number of blocks to process per tick.
     * @param inwardSpeed   Multiplier for the inward (radial) speed.
     * @param swirlSpeed    Multiplier for the swirling (tangential) speed.
     */
    public static void processAbsorptionBatch(ServerWorld world, Vec3d center, List<BlockPos> positions,
                                              Vec3d diskNormal, int blocksPerTick,
                                              double inwardSpeed, double swirlSpeed) {
        int count = Math.min(blocksPerTick, positions.size());
        for (int i = 0; i < count; i++) {
            BlockPos pos = positions.remove(0);
            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.getHardness(world, pos) < 0) continue;

            // Remove the block.
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            Vec3d blockCenter = Vec3d.ofCenter(pos);
            Vec3d radial = center.subtract(blockCenter);
            Vec3d inward = radial.normalize().multiply(inwardSpeed);

            // Project the radial vector onto the disk plane.
            Vec3d radialInPlane = radial.subtract(diskNormal.multiply(radial.dotProduct(diskNormal)));
            if (radialInPlane.lengthSquared() < 1e-4) {
                radialInPlane = diskNormal.crossProduct(new Vec3d(1, 0, 0));
                if (radialInPlane.lengthSquared() < 1e-4) {
                    radialInPlane = diskNormal.crossProduct(new Vec3d(0, 1, 0));
                }
            }
            radialInPlane = radialInPlane.normalize();
            Vec3d tangential = diskNormal.crossProduct(radialInPlane).normalize();
            Vec3d swirl = tangential.multiply(swirlSpeed);
            Vec3d velocity = inward.add(swirl);

            FallingBlockEntity fb = FallingBlockEntity.spawnFromBlock(world, pos, state);
            fb.setVelocity(velocity);
            world.spawnEntity(fb);
        }
    }


//    /**
//     * Processes a subset of blocks from the provided list. For each block,
//     * it removes the block from the world and spawns a falling block entity with a velocity
//     * that combines an inward pull with a swirling (tangential) component.
//     *
//     * @param world        The ServerWorld instance.
//     * @param center       The center point of the effect.
//     * @param positions    The list of block positions to process.
//     * @param diskNormal   The normal vector defining the accretion disk plane.
//     * @param blocksPerTick How many blocks to process per tick.
//     * @param inwardSpeed  The multiplier for the inward (radial) speed.
//     * @param swirlSpeed   The multiplier for the tangential (swirl) speed.
//     */
//    public static void processAbsorptionBlocks(ServerWorld world, Vec3d center, List<BlockPos> positions,
//                                               Vec3d diskNormal, int blocksPerTick,
//                                               double inwardSpeed, double swirlSpeed) {
//        for (int i = 0; i < blocksPerTick && !positions.isEmpty(); i++) {
//            BlockPos pos = positions.removeFirst();
//            BlockState state = world.getBlockState(pos);
//            if (state.isAir() || state.getHardness(world, pos) < 0) continue;
//
//            // Remove the block from the world.
//            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
//            Vec3d blockCenter = Vec3d.ofCenter(pos);
//
//            // Calculate the combined inward and swirling velocity.
//            Vec3d velocity = calcSwirlVelocity(center, blockCenter, diskNormal, inwardSpeed, swirlSpeed);
//
//            // Spawn the falling block entity with the calculated velocity.
//            FallingBlockEntity fb = FallingBlockEntity.spawnFromBlock(world, pos, state);
//            fb.setVelocity(velocity);
//            if (world.random.nextDouble() < 0.9) {
//                fb.dropItem = false;
//            }
//            world.spawnEntity(fb);
//        }
//        // Schedule further processing if there are blocks left.
//        if (!positions.isEmpty()) {
//            world.getServer().execute(() ->
//                    processAbsorptionBlocks(world, center, positions, diskNormal, blocksPerTick, inwardSpeed, swirlSpeed));
//        }
//    }

//    /**
//     * Calculates a velocity vector for a block based on its position relative to the center.
//     * The resulting velocity combines an inward (radial) component with a tangential (swirl) component.
//     *
//     * @param center       The center point of the effect.
//     * @param blockCenter  The center of the block.
//     * @param diskNormal   The normal vector defining the accretion disk plane.
//     * @param inwardSpeed  The multiplier for the inward component.
//     * @param swirlSpeed   The multiplier for the swirl component.
//     * @return A Vec3d representing the final velocity.
//     */
//    public static Vec3d calcSwirlVelocity(Vec3d center, Vec3d blockCenter, Vec3d diskNormal,
//                                          double inwardSpeed, double swirlSpeed) {
//        Vec3d radial = center.subtract(blockCenter);
//        Vec3d inward = radial.normalize().multiply(inwardSpeed);
//
//        // Project the radial vector onto the accretion disk plane.
//        Vec3d radialInPlane = radial.subtract(diskNormal.multiply(radial.dotProduct(diskNormal)));
//        if (radialInPlane.lengthSquared() < 1e-4) {
//            radialInPlane = diskNormal.crossProduct(new Vec3d(1, 0, 0));
//            if (radialInPlane.lengthSquared() < 1e-4) {
//                radialInPlane = diskNormal.crossProduct(new Vec3d(0, 1, 0));
//            }
//        }
//        radialInPlane = radialInPlane.normalize();
//        Vec3d tangential = diskNormal.crossProduct(radialInPlane).normalize();
//        Vec3d swirl = tangential.multiply(swirlSpeed);
//
//        return inward.add(swirl);
//    }

    /**
     * Generates and returns a random unit vector.
     *
     * @param random A Random instance.
     * @return A random unit vector.
     */
    public static Vec3d randomUnitVector(Random random) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double z = random.nextDouble() * 2 - 1;
        double r = Math.sqrt(1 - z * z);
        double x = r * Math.cos(theta);
        double y = r * Math.sin(theta);
        return new Vec3d(x, y, z);
    }
}
