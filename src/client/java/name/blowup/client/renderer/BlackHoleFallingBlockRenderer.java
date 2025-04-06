package name.blowup.client.renderer;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
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
