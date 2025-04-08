package name.blowup.utils;

import name.blowup.blocks.CustomTNTBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Utility class for handling explosions and block destruction in Minecraft.
 * This class provides methods to fling blocks, trigger chain reactions, and calculate velocities.
 */
public class Kaboom {
    private static final java.util.Random random = new java.util.Random();

    /**
     * Fling blocks in a sphere around a center point.
     *
     * @param world The world where the blocks are located.
     * @param center The center point of the sphere.
     * @param flingRadius The radius within which blocks will be flung.
     * @param destructionRadius The radius within which blocks will be destroyed.
     * @param maxY The maximum vertical velocity for the blocks.
     * @param decay The decay factor for the velocity.
     * @param flingChance The chance that a block will be flung.
     */
    public static void destroyAndFlingBlocks(ServerWorld world, Vec3d center, int flingRadius, int destructionRadius, double maxY, double decay, double flingChance) {
        iterateThroughBlocks(center, destructionRadius, pos -> {
            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.getHardness(world, pos) < 0) return;

            boolean isInFlingRadius = BlockPos.ofFloored(center).getSquaredDistance(pos) <= flingRadius * flingRadius;
            boolean shouldFling = isInFlingRadius && world.random.nextDouble() < flingChance;

            if (shouldFling) {
                Vec3d blockCenter = Vec3d.ofCenter(pos);
                Vec3d velocity = calcVelocity(blockCenter, center, maxY, decay);
                FallingBlockEntity fb = FallingBlockEntity.spawnFromBlock(world, pos, state);

                fb.setVelocity(velocity);
                if (world.random.nextDouble() < 0.9) fb.dropItem = false;
                world.spawnEntity(fb);
            }

            // Remove the block and update neighbors.
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        });
    }

    /**
     * Triggers chain reactions by priming any TNT blocks (vanilla or custom)
     * within the given radius from the explosion center.
     *
     * @param world The world where the explosion occurs.
     * @param center The center point of the explosion.
     * @param chainReactionRadius The radius in which TNT blocks should be primed.
     */
    public static void triggerChainReaction(ServerWorld world, Vec3d center, int chainReactionRadius) {
        iterateThroughBlocks(center, chainReactionRadius, pos -> {
            BlockState state = world.getBlockState(pos);
            if (state.isAir()) return;

            // Check if this block is a TNT block (vanilla or custom) and remove and update neighbors.
            if (state.getBlock() instanceof TntBlock) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);

                // For custom TNT blocks, use their priming logic.
                if (state.getBlock() instanceof CustomTNTBlock customTNT) {
                    customTNT.primeChainReactionTntEntity(world, pos, null);
                } else {
                    // For vanilla TNT, create a TntEntity and use the helper.
                    TntEntity vanillaTnt = new TntEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
                    ExplosionUtil.primeChainReactionTntEntity(world, pos, vanillaTnt);
                }
            }
        });
    }

    public static void iterateThroughBlocks(Vec3d center, int radius, Consumer<BlockPos> action) {
        BlockPos origin = BlockPos.ofFloored(center);
        List<BlockPos> positions = new ArrayList<>();

        for (BlockPos pos : BlockPos.iterate(
                origin.add(-radius, -radius, -radius),
                origin.add(radius, radius, radius))) {

            if (origin.getSquaredDistance(pos) <= radius * radius) {
                positions.add(pos.toImmutable()); // Make sure we don't use mutables
            }
        }

        // Sort positions by distance to center (i.e., center-outward iteration)
        positions.sort(Comparator.comparingDouble(origin::getSquaredDistance));

        for (BlockPos pos : positions) {
            action.accept(pos);
        }
    }


    /**
     * Calculates the velocity vector for a block based on its position relative to a center point.
     *
     * @param blockPos The position of the block.
     * @param vecCenter The center point from which the velocity is calculated.
     * @param maxY The maximum vertical velocity.
     * @param decay The decay factor for the velocity.
     * @return A Vec3d representing the calculated velocity.
     */
    public static Vec3d calcVelocity(Vec3d blockPos, Vec3d vecCenter, double maxY, double decay) {
        Vec3d direction = blockPos.subtract(vecCenter);
        double distance = direction.length() == 0 ? 0.001 : direction.length(); // magnitude of the vector
        Vec3d unitDirection = direction.normalize();

        double velocityScale = Math.exp(-decay*distance); // decay factor
        Vec3d horizontalVelocity = unitDirection.multiply(velocityScale * 1.5); // * overall explosion power multiplier

        double yVelocity = velocityScale * maxY * random.nextDouble(); // vertical velocity based on radius

        double clampedY = Math.min(yVelocity, maxY);

        return new Vec3d(horizontalVelocity.x, clampedY, horizontalVelocity.z);
    }

    public static void giveTntHop(TntEntity entity) {
        entity.setVelocity(
                random.nextDouble() * 0.02 - 0.01,
                0.2,
                random.nextDouble() * 0.02 - 0.01
        );
    }
}
