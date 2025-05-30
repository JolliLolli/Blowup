package name.blowup.client.guis;

import name.blowup.guis.DetonatorScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static name.blowup.Blowup.MOD_ID;

// DetonatorScreen.java
public class DetonatorScreen extends HandledScreen<DetonatorScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(MOD_ID, "textures/gui/detonator_gui.png");

    public DetonatorScreen(DetonatorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title);
        this.backgroundWidth = 200;
        this.backgroundHeight = 200;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width  - backgroundWidth)  / 2;
        int y = (height - backgroundHeight) / 2;

        // no client or network handler, can't send packets
        if (client == null || client.getNetworkHandler() == null) return;

        // Timer + button (ID = 0)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("+T"), btn -> {
            // send a packet to the server with button ID 0
            client.getNetworkHandler()
                  .sendPacket(new ButtonClickC2SPacket(handler.syncId, 0));
        }).dimensions(x + 50, y + 20, 20, 20).build());

        // Timer - button (ID = 1)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("–T"), btn -> {
            client.getNetworkHandler()
                  .sendPacket(new ButtonClickC2SPacket(handler.syncId, 1));
        }).dimensions(x + 20, y + 20, 20, 20).build());

        // Detonate button (ID = 2)
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Detonate"), btn -> {
            client.getNetworkHandler()
                  .sendPacket(new ButtonClickC2SPacket(handler.syncId, 2));
        }).dimensions(x + 60, y + 60, 60, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // draw background & slots
        renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        drawMouseoverTooltip(ctx, mouseX, mouseY);

        // draw live counters for R & T
        int x = (width  - backgroundWidth)  / 2;
        int y = (height - backgroundHeight) / 2;
        int radius = handler.getDelegate().get(0);
        int timer  = handler.getDelegate().get(1);

        ctx.drawCenteredTextWithShadow(
            textRenderer,
            "R=" + radius,
            x + backgroundWidth / 2,
            y + 5,
            0xFFFFFF
        );
        ctx.drawCenteredTextWithShadow(
            textRenderer,
            "T=" + timer,
            x + backgroundWidth / 2,
            y + 30,
            0xFFFFFF
        );
    }


    @Override
    protected void drawBackground(DrawContext context,
                                  float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(
                RenderLayer::getGuiTextured,
                TEXTURE,
                x, y,
                0f, 0f,
                backgroundWidth, backgroundHeight,
                backgroundWidth, backgroundHeight
        );
    }
}
