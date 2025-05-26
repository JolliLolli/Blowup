package name.blowup.entities;

import name.blowup.guis.DetonatorScreenHandler;
import name.blowup.registering.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.AnimatableManager.*;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import static name.blowup.Blowup.MOD_ID;
import static software.bernie.geckolib.animation.Animation.LoopType.PLAY_ONCE;

public class DetonatorBlockEntity extends BlockEntity implements GeoBlockEntity, ExtendedScreenHandlerFactory<BlockPos>, Inventory {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation PLUNGE_ANIM = RawAnimation.begin().then("plunge", PLAY_ONCE);

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int activationRadius;
    private int timerTicks = 0;
    private boolean pendingExplosion = false;

    public DetonatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DETONATOR_ENTITY, pos, state);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    // ─── Getters/Setters ────────────────────────────
    public int  getActivationRadius()       { return activationRadius; }
    public void setActivationRadius(int r)  { activationRadius = r; markDirty(); }

    public int  getTimerTicks()             { return timerTicks; }
    public void setTimerTicks(int t)        { timerTicks = t;     markDirty(); }

    public boolean isPendingExplosion()     { return pendingExplosion; }
    public void    setPendingExplosion(boolean p) { pendingExplosion = p; markDirty(); }

    /* ---------- animation plumbing ---------- */
    private static PlayState predicate(AnimationState<DetonatorBlockEntity> state) {
        return state.isMoving() ? PlayState.CONTINUE : PlayState.STOP;
    }

    public void startPlunge() {
        this.triggerAnim("plunge_controller", "plunge");
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        AnimationController<DetonatorBlockEntity> plungeController = new AnimationController<>(
                this,
                "plunge_controller",
                0,
                DetonatorBlockEntity::predicate
        );
        plungeController.triggerableAnim("plunge", PLUNGE_ANIM);
        controllers.add(plungeController);
    }

    /* ---------- gui plumbing ---------- */

    @Override
    public Text getDisplayName() {
        return Text.translatable("Detonator");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new DetonatorScreenHandler(syncId, inv, this.getPos());
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.getPos();
    }

    // ─── Inventory (1 slot) ────────────────────────
    @Override public int size()                    { return 1; }
    @Override public boolean isEmpty()             { return items.getFirst().isEmpty(); }
    @Override public ItemStack getStack(int s)     { return items.get(s); }
    @Override public ItemStack removeStack(int i, int c) {
        return Inventories.splitStack(items, i, c);
    }
    @Override public ItemStack removeStack(int i)  { return Inventories.removeStack(items, i); }
    @Override public void setStack(int i, ItemStack st) { items.set(i, st); }
    @Override public void clear()                  { items.clear(); }
    @Override public void markDirty()              { super.markDirty(); }
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    // ─── Helpers ────────────────────────────────────
    /**
     * Try to burn one piece of redstone fuel.
     * @return true if we actually burned one dust
     */
    public boolean burnFuel() {
        ItemStack st = this.getStack(0);
        if (st.getItem() == Items.REDSTONE && st.getCount() > 0) {
            st.decrement(1);
            // bump the stored radius by 1
            setActivationRadius(getActivationRadius() + 1);
            // markDirty so we’ll save & sync
            markDirty();
            return true;
        }
        return false;
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    protected void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(tag, registries);
        LOGGER.info("[BE] writeNbt: items={}, radius={}, timer={}, pending={}",
                items.getFirst().getCount(),
                activationRadius, timerTicks, pendingExplosion);
        // save our single‐slot inventory
        Inventories.writeNbt(tag, items, registries);
        // save radius, timer, pending flag
        tag.putInt("radius",          activationRadius);
        tag.putInt("timer",           timerTicks);
        tag.putBoolean("pending",     pendingExplosion);
    }

    @Override
    protected void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(tag, registries);
        // load inventory back into the same list instance
        Inventories.readNbt(tag, items, registries);
        // restore our fields
        activationRadius = tag.getInt("radius");
        timerTicks       = tag.getInt("timer");
        pendingExplosion = tag.getBoolean("pending");
    }
}
