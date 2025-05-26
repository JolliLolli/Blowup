package name.blowup.client.renderer.BlackHole;

import name.blowup.client.model.ModelBlackHole;
import name.blowup.entities.BlackHole.BlackHoleEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class BlackHoleRenderer extends EntityRenderer<BlackHoleEntity, BlackHoleEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of("blowup:textures/block/black.png");
    private final ModelBlackHole model;

    public BlackHoleRenderer(EntityRendererFactory.Context context) {
        super(context);

        // Build a ModelPart directly, then create the model
        ModelPart root = ModelBlackHole.createModel();
        this.model = new ModelBlackHole(root);
    }

    @Override
    public BlackHoleEntityRenderState createRenderState() {
        return new BlackHoleEntityRenderState() {};
    }

    @Override
    public void updateRenderState(BlackHoleEntity entity, BlackHoleEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.entity = entity;
    }

    public Identifier getTexture() {
        return TEXTURE;
    }

    @Override
    public void render(BlackHoleEntityRenderState state, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        // For example, if your render state contains the entity, you can retrieve it:
        BlackHoleEntity entity = state.entity;
        float scale = entity.getScale();

        // Push matrix for consistent transform
        matrices.push();
        float anchor = 1.0F; // 1 block offset (adjust if needed)
        matrices.translate(0.0F, anchor * (1.0F - scale) - 0.5F, 0.0F);
        matrices.scale(scale, scale, scale);

        // Render model with regular layer
        this.model.render(
                matrices,
                vertexConsumers.getBuffer(this.model.getLayer(getTexture())),
                light,
                OverlayTexture.DEFAULT_UV,
                1.0F, 1.0F, 1.0F, 1.0F
        );

        // Pop transform to reset
        matrices.pop();

        // Let Minecraft render labels and leashes.
        super.render(state, matrices, vertexConsumers, light);
    }

    @Override
    public boolean shouldRender(BlackHoleEntity entity, Frustum frustum, double x, double y, double z) {
        return true; // Always render, even if off-screen
    }
}
