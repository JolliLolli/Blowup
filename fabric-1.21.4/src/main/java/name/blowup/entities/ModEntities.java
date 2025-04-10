package name.blowup.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * This class is used to register custom entities for the mod.
 * It contains static fields for each entity type, which are registered with the Minecraft registry.
 * The entities include NukeEntity, BlackHoleTNTEntity, and BlackHoleEntity.
 */
public class ModEntities {

    // Custom TNT entities + entities they spawn
    public static final EntityType<NukeEntity> NUKE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("blowup", "nuke"),
            EntityType.Builder.create(NukeEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98f, 0.98f) // specify width and height directly
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("blowup", "nuke")))
    );

    public static final EntityType<BlackHoleTNTEntity> BLACK_HOLE_TNT_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("blowup", "black_hole_tnt"),
            EntityType.Builder.create(BlackHoleTNTEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98f, 0.98f) // specify width and height directly
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("blowup", "black_hole_tnt")))
    );

    public static final EntityType<BlackHoleEntity> BLACK_HOLE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("blowup", "black_hole"),
            EntityType.Builder.create(BlackHoleEntity::new, SpawnGroup.MISC)
                    .dimensions(0.98f, 0.98f) // specify width and height directly
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("blowup", "black_hole")))
    );

    public static final EntityType<BlackHoleFallingBlockEntity> BLACK_HOLE_FALLING_BLOCK_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("blowup", "black_hole_falling_block"),
            EntityType.Builder.<BlackHoleFallingBlockEntity>create(BlackHoleFallingBlockEntity::new, SpawnGroup.MISC)
                    .dimensions(1.0F, 1.0F)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("blowup", "black_hole_falling_block")))
    );

    public static void register() {
        // This method exists solely to force the class to load and its static fields to initialize.
        System.out.println("ModEntities registered");
    }
}
