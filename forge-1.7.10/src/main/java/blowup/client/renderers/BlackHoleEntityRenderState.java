package blowup.client.renderers;

import name.blowup.entities.BlackHoleEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class BlackHoleEntityRenderState extends EntityRenderState {
    public BlackHoleEntity entity;

    public BlackHoleEntityRenderState() {
        super();
    }
}
