package name.blowup;

import name.blowup.blocks.CustomTNTBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

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
        BlockPos origin = BlockPos.ofFloored(center);
        // Loop over the entire destruction sphere.
        for (BlockPos pos : BlockPos.iterate(origin.add(-destructionRadius, -destructionRadius, -destructionRadius),
                                             origin.add(destructionRadius, destructionRadius, destructionRadius))) {
            // Only consider blocks within the destruction radius.
            if (origin.getSquaredDistance(pos) > destructionRadius * destructionRadius) continue;

            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.getHardness(world, pos) < 0) continue;

            // Determine if this block is within the inner (fling) radius.
            boolean isInFlingRadius = origin.getSquaredDistance(pos) <= flingRadius * flingRadius;
            boolean shouldFling = isInFlingRadius && world.random.nextDouble() < flingChance;

            if (shouldFling) {
                Vec3d blockCenter = Vec3d.ofCenter(pos);
                Vec3d velocity = calcVelocity(blockCenter, center, maxY, decay);
                FallingBlockEntity fb = FallingBlockEntity.spawnFromBlock(world, pos, state);

                fb.setVelocity(velocity);
                // Optionally disable item drop.
                if (world.random.nextDouble() < 0.9)
                    fb.dropItem = false;
                world.spawnEntity(fb);
            }

            // Remove the block and update neighbors.
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
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
        BlockPos origin = BlockPos.ofFloored(center);

        // Loop over all blocks within the chain reaction radius.
        for (BlockPos pos : BlockPos.iterate(
                origin.add(-chainReactionRadius, -chainReactionRadius, -chainReactionRadius),
                origin.add(chainReactionRadius, chainReactionRadius, chainReactionRadius))) {

            // Ensure the block is within a spherical radius.
            if (origin.getSquaredDistance(pos) > chainReactionRadius * chainReactionRadius) continue;

            BlockState state = world.getBlockState(pos);
            if (state.isAir()) continue;

            // Check if this block is a TNT block (vanilla or custom).
            if (state.getBlock() instanceof TntBlock) {
                // Remove the block and update neighbors.
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
