package name.blowup.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
@Environment(EnvType.CLIENT)
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class ModelBlackHole extends EntityModel<EntityRenderState> {
	private final ModelPart bone;
	private final ModelPart hexadecagon2;
	private final ModelPart hexadecagon;
	private final ModelPart bone5;
	private final ModelPart hexadecagon8;
	private final ModelPart hexadecagon9;
	private final ModelPart bone3;
	private final ModelPart hexadecagon5;
	private final ModelPart hexadecagon6;
	private final ModelPart bone4;
	private final ModelPart hexadecagon3;
	private final ModelPart hexadecagon7;

	public ModelBlackHole(ModelPart root) {
		super(root);

		this.bone = root.getChild("bone");
		this.hexadecagon2 = bone.getChild("hexadecagon2");
		this.hexadecagon = bone.getChild("hexadecagon");
		this.bone5 = root.getChild("bone5");
		this.hexadecagon8 = bone5.getChild("hexadecagon8");
		this.hexadecagon9 = bone5.getChild("hexadecagon9");
		this.bone3 = root.getChild("bone3");
		this.hexadecagon5 = bone3.getChild("hexadecagon5");
		this.hexadecagon6 = bone3.getChild("hexadecagon6");
		this.bone4 = root.getChild("bone4");
		this.hexadecagon3 = bone4.getChild("hexadecagon3");
		this.hexadecagon7 = bone4.getChild("hexadecagon7");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float offsetY = 24.0F;
		ModelPartData bone = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.origin(0.0F, offsetY, 0.0F));

		ModelPartData hexadecagon2 = bone.addChild("hexadecagon2", ModelPartBuilder.create().uv(8, 12).cuboid(-1.0F, -8.2984F, 6.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F))
		.uv(4, 28).cuboid(-1.0F, -9.5F, 7.7016F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, -8.0F));

		ModelPartData hexadecagon_r1 = hexadecagon2.addChild("hexadecagon_r1", ModelPartBuilder.create().uv(8, 28).cuboid(7.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(0, 16).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r2 = hexadecagon2.addChild("hexadecagon_r2", ModelPartBuilder.create().uv(0, 28).cuboid(7.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(0, 12).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r3 = hexadecagon2.addChild("hexadecagon_r3", ModelPartBuilder.create().uv(16, 0).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r4 = hexadecagon2.addChild("hexadecagon_r4", ModelPartBuilder.create().uv(8, 8).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon = bone.addChild("hexadecagon", ModelPartBuilder.create().uv(0, 8).cuboid(-0.5F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F))
		.uv(24, 20).cuboid(-0.5F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -8.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData hexadecagon_r5 = hexadecagon.addChild("hexadecagon_r5", ModelPartBuilder.create().uv(24, 24).cuboid(8.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(8, 0).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r6 = hexadecagon.addChild("hexadecagon_r6", ModelPartBuilder.create().uv(24, 16).cuboid(8.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(0, 4).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r7 = hexadecagon.addChild("hexadecagon_r7", ModelPartBuilder.create().uv(8, 4).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r8 = hexadecagon.addChild("hexadecagon_r8", ModelPartBuilder.create().uv(0, 0).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData bone5 = modelPartData.addChild("bone5", ModelPartBuilder.create(), ModelTransform.of(0.0F, offsetY, 0.0F, 0.0227F, 0.3927F, 0.0F));

		ModelPartData hexadecagon8 = bone5.addChild("hexadecagon8", ModelPartBuilder.create().uv(8, 12).cuboid(-1.0F, -8.2984F, 6.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F))
		.uv(4, 28).cuboid(-1.0F, -9.5F, 7.7016F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, -8.0F));

		ModelPartData hexadecagon_r9 = hexadecagon8.addChild("hexadecagon_r9", ModelPartBuilder.create().uv(8, 28).cuboid(7.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(0, 16).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r10 = hexadecagon8.addChild("hexadecagon_r10", ModelPartBuilder.create().uv(0, 28).cuboid(7.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(0, 12).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r11 = hexadecagon8.addChild("hexadecagon_r11", ModelPartBuilder.create().uv(16, 0).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r12 = hexadecagon8.addChild("hexadecagon_r12", ModelPartBuilder.create().uv(8, 8).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon9 = bone5.addChild("hexadecagon9", ModelPartBuilder.create().uv(0, 8).cuboid(-0.5F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F))
		.uv(24, 20).cuboid(-0.5F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -8.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData hexadecagon_r13 = hexadecagon9.addChild("hexadecagon_r13", ModelPartBuilder.create().uv(24, 24).cuboid(8.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(8, 0).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r14 = hexadecagon9.addChild("hexadecagon_r14", ModelPartBuilder.create().uv(24, 16).cuboid(8.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(0, 4).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r15 = hexadecagon9.addChild("hexadecagon_r15", ModelPartBuilder.create().uv(8, 4).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r16 = hexadecagon9.addChild("hexadecagon_r16", ModelPartBuilder.create().uv(0, 0).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData bone3 = modelPartData.addChild("bone3", ModelPartBuilder.create(), ModelTransform.of(0.0F, offsetY, 0.0F, -0.0559F, -0.7854F, 0.0175F));

		ModelPartData hexadecagon5 = bone3.addChild("hexadecagon5", ModelPartBuilder.create().uv(16, 20).cuboid(-1.0F, -8.2984F, 6.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F))
		.uv(28, 20).cuboid(-1.0F, -9.5F, 7.7016F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, -8.0F));

		ModelPartData hexadecagon_r17 = hexadecagon5.addChild("hexadecagon_r17", ModelPartBuilder.create().uv(24, 28).cuboid(7.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(0, 24).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r18 = hexadecagon5.addChild("hexadecagon_r18", ModelPartBuilder.create().uv(20, 28).cuboid(7.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(8, 20).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r19 = hexadecagon5.addChild("hexadecagon_r19", ModelPartBuilder.create().uv(24, 0).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r20 = hexadecagon5.addChild("hexadecagon_r20", ModelPartBuilder.create().uv(0, 20).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon6 = bone3.addChild("hexadecagon6", ModelPartBuilder.create().uv(24, 8).cuboid(-0.5F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F))
		.uv(28, 28).cuboid(-0.5F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -8.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData hexadecagon_r21 = hexadecagon6.addChild("hexadecagon_r21", ModelPartBuilder.create().uv(0, 32).cuboid(8.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(24, 12).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r22 = hexadecagon6.addChild("hexadecagon_r22", ModelPartBuilder.create().uv(28, 24).cuboid(8.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(8, 24).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r23 = hexadecagon6.addChild("hexadecagon_r23", ModelPartBuilder.create().uv(16, 24).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r24 = hexadecagon6.addChild("hexadecagon_r24", ModelPartBuilder.create().uv(24, 4).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData bone4 = modelPartData.addChild("bone4", ModelPartBuilder.create(), ModelTransform.of(0.0F, offsetY, 0.0F, -0.0209F, -0.3927F, 0.0052F));

		ModelPartData hexadecagon3 = bone4.addChild("hexadecagon3", ModelPartBuilder.create().uv(16, 20).cuboid(-1.0F, -8.2984F, 6.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F))
		.uv(28, 20).cuboid(-1.0F, -9.5F, 7.7016F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, -8.0F));

		ModelPartData hexadecagon_r25 = hexadecagon3.addChild("hexadecagon_r25", ModelPartBuilder.create().uv(24, 28).cuboid(7.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(0, 24).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r26 = hexadecagon3.addChild("hexadecagon_r26", ModelPartBuilder.create().uv(20, 28).cuboid(7.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(8, 20).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r27 = hexadecagon3.addChild("hexadecagon_r27", ModelPartBuilder.create().uv(24, 0).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r28 = hexadecagon3.addChild("hexadecagon_r28", ModelPartBuilder.create().uv(0, 20).cuboid(7.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.0F, 8.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon7 = bone4.addChild("hexadecagon7", ModelPartBuilder.create().uv(24, 8).cuboid(-0.5F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F))
		.uv(28, 28).cuboid(-0.5F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F)), ModelTransform.of(-0.5F, -8.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData hexadecagon_r29 = hexadecagon7.addChild("hexadecagon_r29", ModelPartBuilder.create().uv(0, 32).cuboid(8.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(24, 12).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r30 = hexadecagon7.addChild("hexadecagon_r30", ModelPartBuilder.create().uv(28, 24).cuboid(8.0F, -1.5F, -0.2984F, 1.0F, 3.0F, 0.5967F, new Dilation(0.0F))
		.uv(8, 24).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r31 = hexadecagon7.addChild("hexadecagon_r31", ModelPartBuilder.create().uv(16, 24).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData hexadecagon_r32 = hexadecagon7.addChild("hexadecagon_r32", ModelPartBuilder.create().uv(24, 4).cuboid(8.0F, -0.2984F, -1.5F, 1.0F, 0.5967F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-8.5F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 48, 48);
	}

	/**
	 * Create a ModelPart directly from the above TexturedModelData.
	 * This bypasses the model-layer approach if it's unavailable.
	 */
	public static ModelPart createModel() {
		TexturedModelData tmd = getTexturedModelData();
		return tmd.createModel();
	}

	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		// You can add your own animation logic here if needed.
	}

	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		this.bone.render(matrices, vertexConsumer, light, overlay);
		this.bone5.render(matrices, vertexConsumer, light, overlay);
		this.bone3.render(matrices, vertexConsumer, light, overlay);
		this.bone4.render(matrices, vertexConsumer, light, overlay);
	}
}