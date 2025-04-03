package name.blowup;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Kaboom {
    private static final java.util.Random random = new java.util.Random();
    /**
     * Fling blocks in a spherical area around a center point.
     * This uses the world, position desired, radius then explosive power to fling blocks.
     *
     * @param world The world where the blocks are located.
     * @param center The center point of the sphere.
     * @param radius The radius of the sphere.
     * @param maxY The maximum vertical velocity.
     * @param decay The decay factor for the velocity.
     * @param skipChance The chance to skip a block (0.0 to 1.0).
     */
    public static void flingBlocksInSphere(ServerWorld world, Vec3d center, int radius, double maxY, double decay, double skipChance) {
        BlockPos origin = BlockPos.ofFloored(center);

        for (BlockPos pos : BlockPos.iterate(origin.add(-radius, -radius, -radius), origin.add(radius, radius, radius))) {
            // Optional: true spherical filter
            if (origin.getSquaredDistance(pos) > radius * radius) continue;

            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.getHardness(world, pos) < 0) continue;
            if (world.random.nextDouble() < skipChance) continue;

            Vec3d blockCenter = Vec3d.ofCenter(pos);
            Vec3d velocity = calcVelocity(blockCenter, center, maxY, decay);

            boolean skipFling = world.random.nextDouble() < skipChance;
            if (!skipFling) {
                FallingBlockEntity fb = FallingBlockEntity.spawnFromBlock(world, pos, state);
                if (fb != null) {
                    fb.setVelocity(velocity);
                    if (random.nextDouble() > 0.1)
                        fb.dropItem = false;
                    world.spawnEntity(fb);
                }
            }

            // Remove block either way
            world.removeBlock(pos, false);

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

}
