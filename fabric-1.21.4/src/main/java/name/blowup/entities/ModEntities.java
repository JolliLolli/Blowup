package name.blowup.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import static name.blowup.Blowup.LOGGER;
import static name.blowup.utils.Registering.*;

/**
 * This class is used to register custom entities for the mod.
 * It contains static fields for each entity type, which are registered with the Minecraft registry.
 * The entities include NukeEntity, BlackHoleTNTEntity, and BlackHoleEntity.
 */
public class ModEntities {

    public static void initialise() {
        // This method exists solely to force the class to load and its static fields to initialize.
        System.out.println("ModEntities registered");
    }

    /**
     * Registers an entity type into the Minecraft registry using consistent structure.
     *
     * @param name     The name/id of the entity.
     * @param group    The entity's spawn group.
     * @param width    The width of the entity's bounding box.
     * @param height   The height of the entity's bounding box.
     * @param factory  A factory method reference (typically a constructor) for the entity.
     * @return The registered EntityType.
     */
    private static <T extends Entity> EntityType<T> register(String name, SpawnGroup group, float width, float height, EntityType.EntityFactory<T> factory) {
        RegistryKey<EntityType<?>> key = keyOfEntity(name);
        EntityType<T> type = EntityType.Builder.create(factory, group)
                .dimensions(width, height)
                .build(key);
        LOGGER.info("Registered entity: {}", name);
        return Registry.register(Registries.ENTITY_TYPE, key, type);
    }

    // === Custom Entity Registrations ===

    public static final EntityType<NukeEntity> NUKE_ENTITY = register(
            "nuke",
            SpawnGroup.MISC,
            0.98f, 0.98f,
            NukeEntity::new
    );

    public static final EntityType<BlackHoleTNTEntity> BLACK_HOLE_TNT_ENTITY = register(
            "black_hole_tnt",
            SpawnGroup.MISC,
            0.98f, 0.98f,
            BlackHoleTNTEntity::new
    );

    public static final EntityType<BlackHoleEntity> BLACK_HOLE_ENTITY = register(
            "black_hole",
            SpawnGroup.MISC,
            0.98f, 0.98f,
            BlackHoleEntity::new
    );

    public static final EntityType<BlackHoleFallingBlockEntity> BLACK_HOLE_FALLING_BLOCK_ENTITY = register(
            "black_hole_falling_block",
            SpawnGroup.MISC,
            1.0f, 1.0f,
            BlackHoleFallingBlockEntity::new
    );
}
