package name.blowup.client.renderer;

import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import java.util.UUID;

public class BlackHoleFallingBlockRenderState extends FallingBlockEntityRenderState {
    // Store the unique ID of the entity to generate per-entity spin variations.
    public UUID uuid;
}
