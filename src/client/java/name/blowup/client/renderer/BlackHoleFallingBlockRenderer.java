package name.blowup.client.renderer;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;

public class BlackHoleFallingBlockRenderer extends FallingBlockEntityRenderer {

    private final BlockRenderManager blockRenderManager;

    public BlackHoleFallingBlockRenderer(EntityRendererFactory.Context context) {
        super(context);
        // Get the block render manager from context
        this.blockRenderManager = context.getBlockRenderManager();
        System.out.println("BlackHoleFallingBlockRenderer instantiated");
    }

    @Override
    public boolean shouldRender(FallingBlockEntity entity, Frustum frustum, double x, double y, double z) {
        BlockState entityState = entity.getBlockState();
        BlockState worldState = entity.getWorld().getBlockState(entity.getFallingBlockPos());
        boolean result = entityState != worldState || !entity.getVelocity().equals(Vec3d.ZERO);
        System.out.println("Client render check — Entity: " + entityState + ", World: " + worldState + "Should render: " + result);
        // Only cull if it's exactly the same *and* the entity isn't moving
        return result;
    }

    @Override
    public void updateRenderState(FallingBlockEntity entity, FallingBlockEntityRenderState state, float partialTicks) {
        // Instead of using boundingBox.maxY, use the entity's Y coordinate
        Vec3d pos = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        BlockPos floored = BlockPos.ofFloored(pos);
        state.fallingBlockPos = entity.getFallingBlockPos();
        state.currentPos = floored;
        state.blockState = entity.getBlockState();
        state.biome = entity.getWorld().getBiome(floored);
        state.world = entity.getWorld();
    }



    @Override
    public void render(FallingBlockEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        BlockState blockState = state.blockState;
        System.out.println("Rendering block: " + blockState);
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            matrices.push();
            // Center the block model on the entity's position
            matrices.translate(-0.5, 0.0, -0.5);

            // Calculate an angle that changes over time (in degrees)
            float angleDegrees = (System.currentTimeMillis() % 36000L) / 100.0F;
            // Convert to radians since rotateAxis() expects radians
            float angleRadians = (float) Math.toRadians(angleDegrees);
            // Create a quaternion that represents a rotation around every axis
            Quaternionf rotation = new Quaternionf().rotateAxis(angleRadians, 1.0F, 1.0F, 1.0F);
            // Multiply the current matrix with the rotation quaternion
            matrices.multiply(rotation);

            // Render the block model using the vanilla BlockRenderManager.
            this.blockRenderManager.getModelRenderer().render(
                    state,
                    this.blockRenderManager.getModel(blockState),
                    blockState,
                    state.currentPos,
                    matrices,
                    vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
                    false,
                    Random.create(),
                    blockState.getRenderingSeed(state.fallingBlockPos),
                    OverlayTexture.DEFAULT_UV
            );
            matrices.pop();
            System.out.println("Block rendered with rotation: " + angleDegrees);
        }
        System.out.println("Block state to be supered: " + blockState);
        // Call super to complete any additional vanilla rendering steps
        super.render(state, matrices, vertexConsumers, light);
    }
}
