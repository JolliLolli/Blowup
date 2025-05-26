package blowup.client.renderers;


import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BlackHoleRenderer extends EntityRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("blowup", "textures/entity/blackhole.png");

    public BlackHoleRenderer(RenderManager renderManager) {
        super(renderManager);
        // Constructor logic if needed
    }

    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Set up the OpenGL state for rendering
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(entityYaw, 0.0F, 1.0F, 0.0F);

        // Render the black hole sphere
        renderSphere(1.0F, 16, 16);

        // Restore the OpenGL state
        GL11.glPopMatrix();
    }

    public void renderSphere(float radius, int stacks, int slices) {
        GL11.glPushMatrix();
        this.deactivateShader();
        GL11.glBegin(GL11.GL_TRIANGLES);

        for (int i = 0; i < stacks; i++) {
            float theta1 = (float)(i * Math.PI / stacks);
            float theta2 = (float)((i + 1) * Math.PI / stacks);

            for (int j = 0; j < slices; j++) {
                float phi1 = (float)(j * 2 * Math.PI / slices);
                float phi2 = (float)((j + 1) * 2 * Math.PI / slices);

                // Compute vertices for the two triangles forming a quad on the sphere surface:
                // Vertex A (theta1, phi1)
                float xA = radius * (float)(Math.sin(theta1) * Math.cos(phi1));
                float yA = radius * (float)Math.cos(theta1);
                float zA = radius * (float)(Math.sin(theta1) * Math.sin(phi1));

                // Vertex B (theta2, phi1)
                float xB = radius * (float)(Math.sin(theta2) * Math.cos(phi1));
                float yB = radius * (float)Math.cos(theta2);
                float zB = radius * (float)(Math.sin(theta2) * Math.sin(phi1));

                // Vertex C (theta2, phi2)
                float xC = radius * (float)(Math.sin(theta2) * Math.cos(phi2));
                float yC = radius * (float)Math.cos(theta2);
                float zC = radius * (float)(Math.sin(theta2) * Math.sin(phi2));

                // Vertex D (theta1, phi2)
                float xD = radius * (float)(Math.sin(theta1) * Math.cos(phi2));
                float yD = radius * (float)Math.cos(theta1);
                float zD = radius * (float)(Math.sin(theta1) * Math.sin(phi2));

                // First triangle: A, B, C
                GL11.glVertex3f(xA, yA, zA);
                GL11.glVertex3f(xB, yB, zB);
                GL11.glVertex3f(xC, yC, zC);

                // Second triangle: A, C, D
                GL11.glVertex3f(xA, yA, zA);
                GL11.glVertex3f(xC, yC, zC);
                GL11.glVertex3f(xD, yD, zD);
            }
        }

        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
