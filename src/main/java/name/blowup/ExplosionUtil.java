package name.blowup;

import name.blowup.entities.CustomTNTEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExplosionUtil {
    public static void playStandardExplosionEffects(ServerWorld world, Vec3d center) {
        world.playSound(null, center.x, center.y, center.z,
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,
                2.0f, 0.8f + world.getRandom().nextFloat() * 0.4f);
        world.spawnParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z,
                10, 0.5, 0.5, 0.5, 0.01);
    }

    public static void primeTnt(World world, BlockPos pos, CustomTNTEntity tntEntity) {
        if (!world.isClient()) {
            Kaboom.giveTntHop(tntEntity);
            world.spawnEntity(tntEntity);
            world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 3f, 1f);
        }
    }

    /**
     * Applies the chain reaction priming logic to any TntEntity.
     *
     * @param world The server world.
     * @param pos The position where the TNT is primed.
     * @param tntEntity The TNT tntEntity (vanilla or custom) to prime.
     */
    public static void primeChainReactionTntEntity(ServerWorld world, BlockPos pos, TntEntity tntEntity) {
        // Center the TNT at the block's center.
        tntEntity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        // Shorten the fuse like vanilla TNT does in chain reactions.
        int baseFuse = tntEntity.getFuse();  // usually 60
        int shortenedFuse = world.random.nextInt(baseFuse / 10) + 4;
        tntEntity.setFuse(shortenedFuse);
        Kaboom.giveTntHop(tntEntity);
        // Spawn the tntEntity and play the priming sound.
        world.spawnEntity(tntEntity);
        world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 3f, 1f);
    }

    // This method makes the explosion fling blocks in a sphere.
    public static void flingExplosion(ServerWorld world, Vec3d center, int flingRadius, int destructionRadius, double maxY, double decay, double flingChance) {
        // Primes other tnt then flings blocks.
        Kaboom.triggerChainReaction(world, center, destructionRadius);
        Kaboom.destroyAndFlingBlocks(world, center, flingRadius, destructionRadius, maxY, decay, flingChance);
    }

    public static void doNukeExplosion(ServerWorld world, Vec3d center) {
        flingExplosion(world, center, 15, 10, 3.0, 0.3, 0.5);
        playStandardExplosionEffects(world, center);
    }
}
