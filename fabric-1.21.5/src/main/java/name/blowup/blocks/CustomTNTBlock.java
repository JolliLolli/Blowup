package name.blowup.blocks;

import name.blowup.entities.CustomTNTEntity;
import name.blowup.utils.ExplosionUtil;
import name.blowup.utils.Kaboom;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public abstract class CustomTNTBlock extends TntBlock {

    public CustomTNTBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        primeTnt(world, pos, player);
        world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 11);

        if (stack.isOf(Items.FLINT_AND_STEEL)) {
            stack.damage(1, player, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        } else {
            stack.decrementUnlessCreative(1, player);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && !player.isCreative()) {
            primeTnt(world, pos, player);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onDestroyedByExplosion(ServerWorld world, BlockPos pos, Explosion explosion) {
        if (!world.isClient()) {
            this.primeChainReactionTntEntity(world, pos, explosion.getCausingEntity() instanceof LivingEntity
                    ? explosion.getCausingEntity() : null);
        }
    }

    /**
     * This method is called when the TNT block is destroyed by an explosion.
     * It primes a chain reaction of TNT entities.
     *
     * @param world The world where the explosion occurred.
     * @param pos The position of the TNT block.
     * @param igniter The entity that caused the explosion, if any.
     */
    public void primeChainReactionTntEntity(ServerWorld world, BlockPos pos, @Nullable LivingEntity igniter) {
        CustomTNTEntity entity = createCustomTNTEntity(world, pos);
        ExplosionUtil.primeChainReactionTntEntity(world, pos, entity);
    }

    /**
     * This method is called when the TNT block is ignited by a player or item.
     * It primes the TNT entity for explosion.
     *
     * @param world The world where the TNT block is located.
     * @param pos The position of the TNT block.
     * @param igniter The entity that ignited the TNT, if any.
     */
    public void primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!world.isClient()) {
            CustomTNTEntity entity = createCustomTNTEntity(world, pos);
            Kaboom.giveTntHop(entity);
            world.spawnEntity(entity);
            world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 3f, 1f);
        }
    }

    // Forces subclasses to implement this method
    protected abstract CustomTNTEntity createCustomTNTEntity(World world, BlockPos pos);
}
