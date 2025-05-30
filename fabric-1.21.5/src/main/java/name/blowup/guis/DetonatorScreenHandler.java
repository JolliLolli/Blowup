package name.blowup.guis;

import name.blowup.entities.DetonatorBlockEntity;
import name.blowup.registering.ModBlocks;
import name.blowup.registering.ModScreenHandlers;
import net.minecraft.block.Block;
import net.minecraft.block.TntBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

// DetonatorScreenHandler.java (common code)
public class DetonatorScreenHandler extends ScreenHandler {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DetonatorScreenHandler.class);

    public ScreenHandlerContext getContext() { return context; }

    private final ScreenHandlerContext context;
    private final PropertyDelegate delegate;

    // IDs for our five buttons:
    private static final int BTN_TIMER_INC  = 0;
    private static final int BTN_TIMER_DEC  = 1;
    private static final int BTN_DETONATE   = 2;

    public DetonatorScreenHandler(int syncId, PlayerInventory inv, BlockPos pos) {
        super(ModScreenHandlers.DETONATOR, syncId);

        // build context & delegate (size = 2: [0]=radius, [1]=timer)
        this.context  = ScreenHandlerContext.create(inv.player.getWorld(), pos);
        this.delegate = new ArrayPropertyDelegate(2);
        this.addProperties(delegate);

        // pull initial values from the BE
        this.context.run((world, bp) -> {
            DetonatorBlockEntity be = (DetonatorBlockEntity)world.getBlockEntity(bp);
            if (be != null) {
                delegate.set(0, be.getActivationRadius());
                delegate.set(1, be.getTimerTicks());
            }

            // bind our one-slot inventory
            this.addSlot(new Slot(be, 0, 80, 54) {
                @Override public boolean canInsert(ItemStack st) {
                    return st.getItem() == Items.REDSTONE;
                }
            });
        });


        // player inventory (3×9)
        final int IX = 8, IY = 84;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, 9 + row*9 + col, IX + col*18, IY + row*18));
            }
        }
        // hotbar (1×9)
        final int HY = IY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, IX + col*18, HY));
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        // Let Minecraft do its normal click logic first:
        super.onSlotClick(slotIndex, button, actionType, player);
        // If they just clicked into your fuel slot (index 0):
        if (slotIndex == 0) {
            // Kick off (or re-kick off) the 10-tick burn loop on the server:
            context.run((world, pos) -> {
                if (world instanceof ServerWorld sw) {
                    sw.scheduleBlockTick(pos, ModBlocks.DETONATOR, 10);
                }
            });
        }
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        context.run((world, bp) -> {
            // Only on the logical server
            if (!(world instanceof ServerWorld sw)) return;

            // Grab your BE (null‐check just in case)
            BlockEntity raw = world.getBlockEntity(bp);
            if (!(raw instanceof DetonatorBlockEntity be)) return;

            // Read the two synced values
            int radius = delegate.get(0);
            int timer = delegate.get(1);

            switch (id) {
                case BTN_TIMER_INC -> {
                    timer++;
                    LOGGER.info("[Handler] Timer ↑ to {}", timer);
                }
                case BTN_TIMER_DEC -> {
                    timer = Math.max(0, timer - 1);
                    LOGGER.info("[Handler] Timer ↓ to {}", timer);
                }
                case BTN_DETONATE -> {
                    // consume 1 redstone, bump radius, then schedule explosion
                    LOGGER.info("[Handler] Detonate clicked: consumed {} dust", radius);
                    if (radius > 0) {
                        // New radius = dust count
                        be.startPlunge();
                        boolean foundTnt = false;
                        for (BlockPos scanPos : BlockPos.iterate(
                                bp.add(-radius, -radius, -radius),
                                bp.add( radius,  radius,  radius)
                        )) {
                            Block block = world.getBlockState(scanPos).getBlock();
                            if (block instanceof TntBlock) {
                                foundTnt = true;
                                break;
                            }
                        }

                        if (foundTnt) {
                            // schedule your explosion after `timer` ticks
                            sw.scheduleBlockTick(bp, ModBlocks.DETONATOR, timer * 20); // 20 ticks = 1 second
                            be.setPendingExplosion(true);
                            // push the BE update to clients
                            sw.getChunkManager().markForUpdate(bp);
                            // also update our GUI slot/property sync (though radius=0)
                            this.sendContentUpdates();

                            LOGGER.info("[Handler] Primed explosion in {} ticks", timer);
                            if (player instanceof ServerPlayerEntity serverPlayer) {
                                serverPlayer.closeHandledScreen();
                            }
                        } else {
                            LOGGER.info("[Handler] No TNT found in radius {}; not priming.", radius);
                        }
                    }
                }
            }

            // write back any changes & sync:
            delegate.set(0, radius);
            delegate.set(1, timer);
            be.setActivationRadius(radius);
            be.setTimerTicks(timer);
        });
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        // optional: implement shift-click logic
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return ScreenHandler.canUse(this.context, player, ModBlocks.DETONATOR);
    }

    public PropertyDelegate getDelegate() {
        return delegate;
    }
}
