package name.blowup.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.AnimatableManager.*;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import static software.bernie.geckolib.animation.Animation.LoopType.PLAY_ONCE;

public class DetonatorBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation PLUNGE_ANIM = RawAnimation.begin().then("plunge", PLAY_ONCE);

    public DetonatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DETONATOR_ENTITY, pos, state);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }


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
}
