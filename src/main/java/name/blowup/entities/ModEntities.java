package name.blowup.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BigTNTEntity> BIG_TNT_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("blowup", "big_tnt"),
            EntityType.Builder.create(BigTNTEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98f, 0.98f) // specify width and height directly
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("blowup", "big_tnt")))
    );

    public static void register() {
        // This method exists solely to force the class to load and its static fields to initialize.
        // Optionally, you can print a log message here for debugging.
        System.out.println("ModEntities registered");
    }
}
