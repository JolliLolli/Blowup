package name.blowup.blocks;

import com.mojang.serialization.MapCodec;
import name.blowup.entities.DetonatorBlockEntity;
import name.blowup.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.blowup.utils.Kaboom.triggerChainReaction;

public class DetonatorBlock extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty PRESSED = BooleanProperty.of("pressed");

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        return (dir == Direction.EAST || dir == Direction.WEST) ? EW_SHAPE : NS_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // collisions don’t need directionality (same for N/S and E/W)
        return NS_SHAPE;
    }

    public DetonatorBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(PRESSED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PRESSED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {

        if (world.isClient) return ActionResult.SUCCESS;

        // forbid re-pressing
        if (state.get(PRESSED)) return ActionResult.CONSUME;

        // 1) animate the handle
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof DetonatorBlockEntity det) det.startPlunge();

        // 2) update blockstate for visual feedback / redstone
        world.setBlockState(pos, state.with(PRESSED, true), Block.NOTIFY_ALL);
        world.scheduleBlockTick(pos, this, 20);
        world.updateNeighbors(pos, this);   // emit redstone if you like

        // 3) …your custom explosion trigger logic here…
        if (!world.isClient()) {
            triggerChainReaction((ServerWorld) world, pos.toCenterPos(), 15);
        }

        return ActionResult.CONSUME;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // reset the button so it can be pressed again
        if (state.get(PRESSED)) {
            world.setBlockState(pos, state.with(PRESSED, false), Block.NOTIFY_ALL);
            world.updateNeighbors(pos, this);
        }
    }

    @Override
    protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        if (!world.isClient) return;

        // Use the new particle‐only item so we know for sure it has a sprite
        ItemStackParticleEffect effect = new ItemStackParticleEffect(
                ParticleTypes.ITEM,
                new ItemStack(ModItems.DETONATOR_PARTICLE)
        );

        Vec3d center = pos.toCenterPos();
        for (int i = 0; i < 35; i++) {
            double dx = center.x + (world.random.nextDouble() - 0.5) * 0.6;
            double dy = center.y + (world.random.nextDouble() - 0.5) * 0.6;
            double dz = center.z + (world.random.nextDouble() - 0.5) * 0.6;

            double vx = (world.random.nextDouble() - 0.5) * 0.2;
            double vy = world.random.nextDouble() * 0.2 + 0.02;
            double vz = (world.random.nextDouble() - 0.5) * 0.2;

            // spawn using the forced item‐sprite
            world.addParticle(effect, false, false, dx, dy, dz, vx, vy, vz);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DetonatorBlockEntity(pos, state);
    }

    // ─── North/South shape ────────────────────────────────────────────
    private static final VoxelShape NS_LAYER1 = Block.createCuboidShape( 0.0,  0.0,  2.0, 16.0,  3.0, 14.0);
    private static final VoxelShape NS_LAYER2 = Block.createCuboidShape( 1.0,  3.0,  3.0, 15.0,  5.0, 13.0);
    private static final VoxelShape NS_LAYER3 = Block.createCuboidShape( 2.0,  5.0,  4.0, 14.0,  7.0, 12.0);
    private static final VoxelShape NS_LAYER4 = Block.createCuboidShape( 4.0,  7.0,  6.0, 12.0,  9.0, 10.0);
    private static final VoxelShape NS_LAYER5 = Block.createCuboidShape( 6.75, 8.0,  7.0,  9.25, 16.0,  9.0);
    private static final VoxelShape NS_LAYER6 = Block.createCuboidShape( 3.0, 14.75, 6.75, 13.0, 17.0,  9.0);

    private static final VoxelShape NS_SHAPE = VoxelShapes.union(
            NS_LAYER1, NS_LAYER2, NS_LAYER3, NS_LAYER4, NS_LAYER5, NS_LAYER6
    );

    // ─── East/West shape (NS rotated 90° around Y) ─────────────────
    private static final VoxelShape EW_LAYER1 = Block.createCuboidShape( 2.0,  0.0,  0.0, 14.0,  3.0, 16.0);
    private static final VoxelShape EW_LAYER2 = Block.createCuboidShape( 3.0,  3.0,  1.0, 13.0,  5.0, 15.0);
    private static final VoxelShape EW_LAYER3 = Block.createCuboidShape( 4.0,  5.0,  2.0, 12.0,  7.0, 14.0);
    private static final VoxelShape EW_LAYER4 = Block.createCuboidShape( 6.0,  7.0,  4.0, 10.0,  9.0, 12.0);
    private static final VoxelShape EW_LAYER5 = Block.createCuboidShape( 7.0,  8.0,  6.75,  9.0, 16.0,  9.25);
    private static final VoxelShape EW_LAYER6 = Block.createCuboidShape( 7.0, 14.75,  3.0,  9.25,17.0, 13.0);

    private static final VoxelShape EW_SHAPE = VoxelShapes.union(
            EW_LAYER1, EW_LAYER2, EW_LAYER3, EW_LAYER4, EW_LAYER5, EW_LAYER6
    );
}
