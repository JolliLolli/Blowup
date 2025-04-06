package name.blowup.client.renderer;

import name.blowup.entities.BlackHoleFallingBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class BlackHoleFallingBlockEntityRenderState extends EntityRenderState {
    public BlackHoleFallingBlockEntity entity;

    public BlackHoleFallingBlockEntityRenderState() {
        super();
    }
}