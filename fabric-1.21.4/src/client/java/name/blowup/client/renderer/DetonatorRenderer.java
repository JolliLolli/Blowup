package name.blowup.client.renderer;

import name.blowup.client.model.DetonatorModel;
import name.blowup.entities.DetonatorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@Environment(EnvType.CLIENT)
public class DetonatorRenderer extends GeoBlockRenderer<DetonatorBlockEntity> {
    public DetonatorRenderer() { super(new DetonatorModel()); }
}
