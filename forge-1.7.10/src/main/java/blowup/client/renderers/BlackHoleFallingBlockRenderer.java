package blowup.client.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class BlackHoleFallingBlockRenderer extends FallingBlockEntityRenderer {
    private final BlockRenderManager blockRenderManager;

    public BlackHoleFallingBlockRenderer(EntityRendererFactory.Context context) {
        super(context);
        // Get the block render manager from context
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public boolean shouldRender(FallingBlockEntity entity, Frustum frustum, double x, double y, double z) {
        BlockState entityState = entity.getBlockState();
        BlockState worldState = entity.getWorld().getBlockState(entity.getFallingBlockPos());
        return entityState != worldState || !entity.getVelocity().equals(Vec3d.ZERO);
    }

    @Override
    public FallingBlockEntityRenderState createRenderState() {
        return new BlackHoleFallingBlockRenderState();
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

        if (state instanceof BlackHoleFallingBlockRenderState bhState) {
            bhState.uuid = entity.getUuid(); // store entity's uuid
        }
    }

    @Override
    public void render(FallingBlockEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!(state instanceof BlackHoleFallingBlockRenderState bhState)) {
            return;
        }
        BlockState blockState = state.blockState;
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            matrices.push();
            // Center the block model on the entity's position
            matrices.translate(-0.5, 0.0, -0.5);

            // Use the entity's uuid from the render state to generate a spin seed.
            long seed = bhState.uuid.getLeastSignificantBits();
            Random random = Random.create(seed);
            Vector3f axis = new Vector3f(
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1,
                    random.nextFloat() * 2 - 1
            ).normalize();
            float offset = random.nextFloat() * 360.0f;

            // Rotation that progresses over time, with entity-specific offset
            float angle = (System.currentTimeMillis() % 36000L + offset) / 10.0F;
            Quaternionf rotation = new Quaternionf().rotateAxis(
                    (float) Math.toRadians(angle),
                    axis.x(), axis.y(), axis.z()
            );
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
        }
    }
}
