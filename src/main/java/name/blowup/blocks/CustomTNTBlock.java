package name.blowup.blocks;

import name.blowup.ExplosionUtil;
import name.blowup.entities.CustomTNTEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.EquipmentSlot;

public abstract class CustomTNTBlock extends TntBlock {

    public CustomTNTBlock(Settings settings) {
        super(settings);
    }

    // Subclasses will implement their custom explosion behavior.
    public abstract void explode(ServerWorld world, Vec3d center);

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

    public void primeChainReactionTntEntity(ServerWorld world, BlockPos pos, @Nullable LivingEntity igniter) {
        // Create your custom TNT entity.
        CustomTNTEntity entity = createCustomTNTEntity(world, pos);
        // Delegate to the generic helper.
        ExplosionUtil.primeChainReactionTntEntity(world, pos, entity);
    }

    // Generic method to prime a TNT entity.
    public void primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!world.isClient()) {
            CustomTNTEntity entity = createCustomTNTEntity(world, pos);
            ExplosionUtil.primeTnt(world, pos, entity);
        }
    }

    // Subclasses provide their own entity creation, allowing for different entity types.
    protected abstract CustomTNTEntity createCustomTNTEntity(World world, BlockPos pos);
}
