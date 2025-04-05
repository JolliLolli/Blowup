package name.blowup.item;

import name.blowup.utils.ExplosionUtil;
import name.blowup.utils.Kaboom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/**
 * Represents a custom sword item that can set entities on fire and trigger explosions that can trigger other tnts.
 * This class extends the SwordItem class to provide specific behavior for the big sword.
 */
public class BigSwordItem extends SwordItem {

    public BigSwordItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.postHit(stack, target, attacker);
        // Example: Apply a custom status effect (like slowing them down).
//        if (attacker instanceof PlayerEntity) {
//            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));
//        }

        // You can also spawn particles, play a sound, or trigger an explosion here.
        // For particles: Use target.world.spawnParticles(...);
        if (!target.getWorld().isClient()) {
            World world = target.getWorld();
            target.setOnFireFor(5);
            world.playSound(
                    null,
                    target.getX(), target.getY(), target.getZ(),
                    SoundEvents.BLOCK_CHERRY_SAPLING_BREAK,
                    target.getSoundCategory(),
                    2.5f, 0.8f + world.getRandom().nextFloat() * 0.4f
            );

            // Explode (without particles) and fling blocks in a sphere.
            ExplosionUtil.flingExplosion((ServerWorld) world, target.getPos(), 3, 3, 0.8, 0.7, 0.6);
        }
        return result;
    }
}
