package blowup.utils;

import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import blowup.blocks.CustomTNTBlock;
import blowup.entities.CustomTNTEntity;

public class Kaboom {
    private static final Random random = new Random();

    // Define a simple functional interface to work with block coordinates.
    public interface BlockAction {
        void accept(int x, int y, int z);
    }

    /**
     * Fling blocks in a sphere around a center point.
     *
     * @param world The world where the blocks are located.
     * @param center The center point of the sphere (a Vec3 with your explosion center).
     * @param flingRadius The radius within which blocks will be flung.
     * @param destructionRadius The overall radius in which blocks will be removed.
     * @param maxY The maximum vertical velocity for the blocks.
     * @param decay The decay factor for the velocity.
     * @param flingChance The chance (0–1) that a block will be flung.
     */
    public static void destroyAndFlingBlocks(World world, Vec3 center, int flingRadius, int destructionRadius, double maxY, double decay, double flingChance) {
        iterateThroughBlocks(center, destructionRadius, new BlockAction() {
            @Override
            public void accept(int x, int y, int z) {
                Block block = world.getBlock(x, y, z);
                if (block == Blocks.air || block.getBlockHardness(world, x, y, z) < 0) return;

                // Calculate squared distance from block center to explosion center.
                double dx = (x + 0.5) - center.xCoord;
                double dy = (y + 0.5) - center.yCoord;
                double dz = (z + 0.5) - center.zCoord;
                double sqDist = dx * dx + dy * dy + dz * dz;

                boolean isInFlingRadius = sqDist <= flingRadius * flingRadius;
                boolean shouldFling = isInFlingRadius && random.nextDouble() < flingChance;

                if (shouldFling) {
                    // Create a vector from the block's center.
                    Vec3 blockCenter = Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5);
                    Vec3 velocity = calcVelocity(blockCenter, center, maxY, decay);

                    EntityFallingBlock fb = new EntityFallingBlock(world, x, y, z, block, world.getBlockMetadata(x, y, z));
                    fb.motionX = velocity.xCoord;
                    fb.motionY = velocity.yCoord;
                    fb.motionZ = velocity.zCoord;

                    // In 1.7.10, the field is typically named "dropItem" (or similar) – adjust as needed.
                    fb.field_145813_c = (random.nextDouble() >= 0.9);

                    world.spawnEntityInWorld(fb);
                }

                // Remove the block from the world.
                world.setBlockToAir(x, y, z);
            }
        });
    }

    /**
     * Triggers chain reactions by priming any TNT blocks (vanilla or custom)
     * within the given radius from the explosion center.
     *
     * @param world The world where the explosion occurs.
     * @param center The explosion’s center as a Vec3.
     * @param chainReactionRadius The radius in which TNT blocks should be primed.
     */
    public static void triggerChainReaction(World world, Vec3 center, int chainReactionRadius) {
        iterateThroughBlocks(center, chainReactionRadius, new BlockAction() {
            @Override
            public void accept(int x, int y, int z) {
                Block block = world.getBlock(x, y, z);
                if (block == Blocks.air) return;

                if (block instanceof BlockTNT) {
                    world.setBlockToAir(x, y, z);

                    if (block instanceof CustomTNTBlock) {
                        // For custom TNT blocks, call their chain-reaction method.
                        ((CustomTNTBlock) block).primeChainReactionTntEntity(world, x, y, z, null);
                    } else {
                        // For vanilla TNT, create an EntityTNTPrimed and process it (using an ExplosionUtil helper, for example).
                        EntityTNTPrimed vanillaTnt = new EntityTNTPrimed(world, x + 0.5, y, z + 0.5, null);
                        ExplosionUtil.primeChainReactionTntEntity(world, x, y, z, vanillaTnt);
                    }
                }
            }
        });
    }

    /**
     * Iterates through blocks in a cube centered on a given Vec3, then performs a distance check.
     * For each block inside the sphere, the action.accept(x,y,z) is called.
     *
     * @param center The center point as a Vec3.
     * @param radius The radius of the sphere.
     * @param action A BlockAction to perform for each block.
     */
    public static void iterateThroughBlocks(Vec3 center, int radius, BlockAction action) {
        int cx = (int) Math.floor(center.xCoord);
        int cy = (int) Math.floor(center.yCoord);
        int cz = (int) Math.floor(center.zCoord);

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    double dx = (x + 0.5) - center.xCoord;
                    double dy = (y + 0.5) - center.yCoord;
                    double dz = (z + 0.5) - center.zCoord;
                    if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                        action.accept(x, y, z);
                    }
                }
            }
        }
    }

    /**
     * Calculates the velocity for a block based on its center relative to the explosion's center.
     *
     * @param blockPos The block's center as a Vec3.
     * @param vecCenter The explosion's center.
     * @param maxY The maximum vertical velocity.
     * @param decay The decay factor.
     * @return A Vec3 representing the velocity.
     */
    public static Vec3 calcVelocity(Vec3 blockPos, Vec3 vecCenter, double maxY, double decay) {
        Vec3 direction = blockPos.subtract(vecCenter);
        double distance = direction.lengthVector();
        if (distance < 0.001) distance = 0.001;
        Vec3 normalized = direction.normalize();

        double velocityScale = Math.exp(-decay * distance);
        double horizX = normalized.xCoord * velocityScale * 1.5;
        double horizZ = normalized.zCoord * velocityScale * 1.5;
        double yVelocity = velocityScale * maxY * random.nextDouble();
        double clampedY = Math.min(yVelocity, maxY);

        return Vec3.createVectorHelper(horizX, clampedY, horizZ);
    }

    /**
     * Gives a little nudge to a TNT entity by setting its motion.
     *
     * @param entity The TNT entity (EntityTNTPrimed) to modify.
     */
    public static void giveTntHop(EntityTNTPrimed entity) {
        entity.motionX = random.nextDouble() * 0.02 - 0.01;
        entity.motionY = 0.2;
        entity.motionZ = random.nextDouble() * 0.02 - 0.01;
    }
}
