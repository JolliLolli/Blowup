package name.blowup.client.model;

import name.blowup.entities.DetonatorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

@Environment(EnvType.CLIENT)
public class DetonatorModel extends GeoModel<DetonatorBlockEntity> {
    private static final String MODID = "blowup";

    @Override
    public Identifier getModelResource(DetonatorBlockEntity detonatorBlockEntity, @Nullable GeoRenderer<DetonatorBlockEntity> geoRenderer) {
        return Identifier.of(MODID, "geo/detonator.geo.json");
    }

    @Override
    public Identifier getTextureResource(DetonatorBlockEntity detonatorBlockEntity, @Nullable GeoRenderer<DetonatorBlockEntity> geoRenderer) {
        return Identifier.of(MODID, "textures/block/detonator.png");
    }

    @Override
    public Identifier getAnimationResource(DetonatorBlockEntity animatable) {
        return Identifier.of(MODID, "animations/detonator.animation.json");
    }

    @Override
    public RenderLayer getRenderType(DetonatorBlockEntity animatable, Identifier texture) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}