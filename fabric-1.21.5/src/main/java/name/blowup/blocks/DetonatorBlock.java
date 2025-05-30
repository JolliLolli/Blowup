package name.blowup.blocks;

import com.mojang.serialization.MapCodec;
import name.blowup.entities.DetonatorBlockEntity;
import name.blowup.guis.DetonatorScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static name.blowup.Blowup.MOD_ID;
import static name.blowup.utils.Kaboom.triggerChainReaction;

public class DetonatorBlock extends BlockWithEntity {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty PRESSED = BooleanProperty.of("pressed");
    public static final BooleanProperty POWERED = Properties.POWERED;

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        return (dir == Direction.EAST || dir == Direction.WEST) ? EW_SHAPE : NS_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return NS_SHAPE;
    }

    public DetonatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(PRESSED, false)
                .with(POWERED, false)
        );
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PRESSED, POWERED);
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

        // Grab your block entity and, if it supports screens, open it:
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ExtendedScreenHandlerFactory<?> factory) {
            player.openHandledScreen(factory);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    @Override
    public void neighborUpdate(BlockState state,
                               World world,
                               BlockPos pos,
                               Block sourceBlock,
                               @Nullable WireOrientation wireOrientation,
                               boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, wireOrientation, notify);
        if (world.isClient) return;

        boolean powered = world.isReceivingRedstonePower(pos);
        boolean pressed = state.get(PRESSED);
        if (powered && !pressed) activateByRedstone(state, world, pos);
    }

    private void activateByRedstone(BlockState state, World world, BlockPos pos) {
        if (world.isClient()) return;

        BlockEntity beRaw = world.getBlockEntity(pos);
        if (!(beRaw instanceof DetonatorBlockEntity be)) return;

        be.startPlunge();
        int radius = be.getActivationRadius();
        int timer = be.getTimerTicks() * 20; // this is in seconds
        if (radius <= 0) return;

        boolean foundTnt = false;
        for (BlockPos scanPos : BlockPos.iterate(
                pos.add(-radius, -radius, -radius),
                pos.add( radius,  radius,  radius)
        )) {
            Block block = world.getBlockState(scanPos).getBlock();
            if (block instanceof TntBlock) {
                foundTnt = true;
                break;
            }
        }

        if (foundTnt) {
            if (world instanceof ServerWorld sw) {
                sw.scheduleBlockTick(pos, this, timer);
                be.setPendingExplosion(true);
            }

            // prevent retriggering until re-armed
            world.setBlockState(pos, state.with(PRESSED, true), Block.NOTIFY_ALL);
        }
    }


    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos,
                             BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 10);
        }
    }

    @Override
    public void scheduledTick(BlockState state,
                              ServerWorld world,
                              BlockPos pos,
                              Random random) {
        BlockEntity raw = world.getBlockEntity(pos);
        if (!(raw instanceof DetonatorBlockEntity be)) return;

        // 1) Fuel‐tick: burn one dust every 10 ticks, reschedule if still has fuel
        if (be.burnFuel()) {
            LOGGER.info("[Block] Burned 1 fuel → radius now {}", be.getActivationRadius());
            // re‐schedule next fuel‐burn
            world.scheduleBlockTick(pos, this, 10);

            // ── SYNC HANDLER TO CLIENT ───────────────────────
            // for each player with our GUI open, push the new slot + delegate!
            for (ServerPlayerEntity sp : world.getServer().getPlayerManager().getPlayerList()) {
                if (sp.currentScreenHandler instanceof DetonatorScreenHandler handler
                        && handler.getContext().get((w, bp) -> bp.equals(pos)).orElse(false)) {
                    handler.sendContentUpdates();
                }
            }
        }

        // 2) Explosion‐tick: only if primed by GUI or redstone
        if (be.isPendingExplosion()) {
            be.setPendingExplosion(false);
            int r = be.getActivationRadius();
            LOGGER.info("[Block] Exploding radius {}", r);
            triggerChainReaction(world, pos.toCenterPos(), r);
            be.setActivationRadius(0);
            be.markDirty();
        }

        // 3) Re‐arm the “pressed” property so it can be activated again
        if (state.get(PRESSED)) {
            world.setBlockState(pos, state.with(PRESSED, false), Block.NOTIFY_ALL);
            world.updateNeighbors(pos, this);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);

        if (world instanceof World w && !w.isClient) {
            w.playSound(
                    null,               // no specific player
                    pos,
                    SoundEvents.BLOCK_STONE_BREAK,
                    SoundCategory.BLOCKS,
                    1.0f,               // volume
                    1.0f                // pitch
            );
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
