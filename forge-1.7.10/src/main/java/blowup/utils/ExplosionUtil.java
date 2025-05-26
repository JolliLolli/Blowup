package blowup.utils;

import blowup.entities.BlackHoleEntity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Utility class for handling explosions and block destruction in Minecraft 1.7.10.
 * This version uses net.minecraft.util.Vec3, raw integer coordinates, and older methods
 * for sounds and particles.
 */
public class ExplosionUtil {

    /**
     * Plays a standard explosion sound and spawns explosion particles.
     * In 1.7.10 we use playSoundEffect and spawnParticle instead of modern calls.
     *
     * @param world  The world object.
     * @param center The center of the explosion as a Vec3.
     */
    public static void playStandardExplosionEffects(World world, Vec3 center) {
        // Play the explosion sound at the center.
        world.playSoundEffect(center.xCoord, center.yCoord, center.zCoord, "random.explode",
            2.0F, 0.8F + world.rand.nextFloat() * 0.4F);

        // Spawn explosion particles manually.
        // Loop a fixed number of times (here 10) to spawn particles with slight random offsets.
        for (int i = 0; i < 10; i++) {
            double offsetX = (world.rand.nextDouble() - 0.5) * 0.5;
            double offsetY = (world.rand.nextDouble() - 0.5) * 0.5;
            double offsetZ = (world.rand.nextDouble() - 0.5) * 0.5;
            world.spawnParticle("explode",
                center.xCoord, center.yCoord, center.zCoord,
                offsetX, offsetY, offsetZ);
        }
    }

    /**
     * Primes a TNT entity for a chain reaction.
     * In 1.7.10, we use int coordinates and EntityTNTPrimed.
     *
     * @param world     The world object.
     * @param x         The x coordinate of the block.
     * @param y         The y coordinate.
     * @param z         The z coordinate.
     * @param tntEntity The TNT entity to prime.
     */
    public static void primeChainReactionTntEntity(World world, int x, int y, int z, EntityTNTPrimed tntEntity) {
        // Center the TNT entity on the block's center.
        tntEntity.setPosition(x + 0.5, y, z + 0.5);

        // Shorten the fuse similarly to vanilla chain reactions.
        int baseFuse = tntEntity.fuse;  // typically 60
        int shortenedFuse = world.rand.nextInt(baseFuse / 10) + 4;
        tntEntity.fuse = shortenedFuse;

        // Apply a small velocity to the TNT using our Kaboom helper.
        Kaboom.giveTntHop(tntEntity);

        // Spawn the TNT and play the priming sound.
        world.spawnEntityInWorld(tntEntity);
        world.playSoundEffect(x + 0.5, y, z + 0.5, "game.tnt.primed", 3.0F, 1.0F);
    }

    /**
     * Executes a "nuke" explosion that first primes nearby TNT blocks and then flings blocks outward.
     *
     * @param world              The world object.
     * @param center             The explosion center as a Vec3.
     * @param flingRadius        Radius within which blocks are flung.
     * @param destructionRadius  Overall radius in which blocks are affected.
     * @param maxY               Maximum upward velocity.
     * @param decay              Decay factor for the velocity.
     * @param flingChance        Chance (0–1) a block gets flung.
     */
    public static void flingExplosion(World world, Vec3 center, int flingRadius, int destructionRadius,
                                      double maxY, double decay, double flingChance) {
        // Prime nearby TNT (both vanilla and custom) and then destroy and fling blocks.
        Kaboom.triggerChainReaction(world, center, destructionRadius);
        Kaboom.destroyAndFlingBlocks(world, center, flingRadius, destructionRadius, maxY, decay, flingChance);
    }

    /**
     * Executes a nuke explosion by flinging blocks and playing explosion effects.
     *
     * @param world  The world object.
     * @param center The center of the explosion as a Vec3.
     */
    public static void doNukeExplosion(World world, Vec3 center) {
        flingExplosion(world, center, 15, 10, 3.0, 0.3, 0.5);
        playStandardExplosionEffects(world, center);
    }

    /**
     * Executes a black hole explosion by playing effects and spawning a BlackHoleEntity.
     *
     * @param world  The world object.
     * @param center The explosion center as a Vec3.
     */
    public static void doBlackHoleExplosion(World world, Vec3 center) {
        playStandardExplosionEffects(world, center);

        // Create and spawn the BlackHoleEntity immediately (no scheduled task in 1.7.10).
        BlackHoleEntity blackHole = new BlackHoleEntity();
        blackHole.setPosition(center.xCoord, center.yCoord, center.zCoord);
        world.spawnEntityInWorld(blackHole);
    }
}
