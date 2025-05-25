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
import static software.bernie.geckolib.animation.PlayState.*;

public class DetonatorBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ANIM_PLUNGE =
            RawAnimation.begin().then("plunge", PLAY_ONCE);   // name must match your animation.json

    public DetonatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DETONATOR_ENTITY, pos, state);
    }

    private static PlayState handle(AnimationState<DetonatorBlockEntity> state) {     // predicate
        state.setAnimation(RawAnimation.begin()
                .then("plunge", PLAY_ONCE));
        return CONTINUE;
    }

    /* ---------- animation plumbing ---------- */

    public void startPlunge() {
        if (world != null && !world.isClient) {
            this.triggerAnim("controller", "plunge");
        }
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this,          // animatable
                        "controller",  // controller name (matches Blockbench)
                        0,             // update period
                        DetonatorBlockEntity::handle)
                        .triggerableAnim("plunge", ANIM_PLUNGE)            // lets triggerAnim() find it
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
