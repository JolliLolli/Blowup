package blowup.entities;

import blowup.Blowup;
import cpw.mods.fml.common.registry.EntityRegistry;

public class ModEntities {
    public static void init() {
        EntityRegistry.registerModEntity(BlackHoleEntity.class, "BlackHoleEntity", 0, Blowup.instance, 64, 30, false);
        EntityRegistry.registerModEntity(NukeEntity.class, "NukeEntity", 1, Blowup.instance, 64, 10, true);
    }
}
