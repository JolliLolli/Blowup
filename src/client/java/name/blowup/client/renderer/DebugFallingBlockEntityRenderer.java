package name.blowup.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

public class DebugFallingBlockEntityRenderer extends FallingBlockEntityRenderer {

    public DebugFallingBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(FallingBlockEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        System.out.println("Rendering falling block entity at position: " + state.currentPos + ", state: " + state.blockState);
        super.render(state, matrices, vertexConsumers, light);
    }
}
