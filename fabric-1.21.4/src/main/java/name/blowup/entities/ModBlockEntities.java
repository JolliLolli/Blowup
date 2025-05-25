package name.blowup.entities;

import name.blowup.blocks.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import static name.blowup.Blowup.LOGGER;
import static name.blowup.utils.Registering.*;

/**
 * Registers all custom block entities for the Blowup mod.
 */
public class ModBlockEntities {

    /**
     * Forces this class to load and its static fields to initialize.
     */
    public static void initialise() {
        System.out.println("ModBlockEntities registered");
    }

    /**
     * Registers a block entity type into the Minecraft registry.
     *
     * @param name     The registry name/id.
     * @param factory  The constructor reference for the block entity.
     * @param blocks   The blocks that use this block entity.
     * @return The registered BlockEntityType.
     */
    private static <T extends BlockEntity> BlockEntityType<T> register(String name, Factory<T> factory, Block... blocks) {
        RegistryKey<BlockEntityType<?>> key = keyOfBlockEntity(name);
        BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(factory, blocks)
                .build();
        LOGGER.info("Registered block entity: {}", name);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, key, type);
    }

    // === Custom Block Entity Registrations ===
    public static final BlockEntityType<DetonatorBlockEntity> DETONATOR_ENTITY = register(
            "detonator",
            DetonatorBlockEntity::new,
            ModBlocks.DETONATOR
    );
}
