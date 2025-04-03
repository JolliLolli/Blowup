package name.blowup.blocks;

import name.blowup.Kaboom;
import name.blowup.entities.BigTNTEntity;
import name.blowup.entities.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class BigTNT extends TntBlock {
    public BigTNT(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        // Custom priming logic
        primeTnt(world, pos);
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11); // replace TNT with air

        if (stack.isOf(Items.FLINT_AND_STEEL)) {
            stack.damage(1, player);
        } else {
            stack.decrementUnlessCreative(1, player);
        }

        return ActionResult.SUCCESS;
    }


    public static void explode(ServerWorld world, Vec3d center) {
        Kaboom.flingBlocksInSphere(world, center, 15, 1.0, 1.0, 0.7);

        // Add some particles and sound at the center
        world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 2.0f, 0.8f + world.getRandom().nextFloat() * 0.4f);
        world.spawnParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 10, 0.5, 0.5, 0.5, 0.01);
    }

    public static void primeTnt(World world, BlockPos pos) {
        if (!world.isClient) {
            BigTNTEntity entity = new BigTNTEntity(ModEntities.BIG_TNT_ENTITY, world);
            entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            entity.setFuse(60); // default fuse
            world.spawnEntity(entity);

            world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 3f, 1f);
        }
    }

}
