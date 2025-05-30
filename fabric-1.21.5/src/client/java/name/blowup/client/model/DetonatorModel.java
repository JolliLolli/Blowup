package name.blowup.client.model;

import name.blowup.entities.DetonatorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(EnvType.CLIENT)
public class DetonatorModel extends GeoModel<DetonatorBlockEntity> {
    private static final String MODID = "blowup";

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return Identifier.of(MODID, "detonator");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return Identifier.of(MODID, "textures/block/detonator.png");
    }

    @Override
    public Identifier getAnimationResource(DetonatorBlockEntity animatable) {
        return Identifier.of(MODID, "detonator");
    }
}