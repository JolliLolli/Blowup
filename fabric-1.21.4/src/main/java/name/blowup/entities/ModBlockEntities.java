package name.blowup.entities;

import name.blowup.blocks.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<DetonatorBlockEntity> DETONATOR_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("blowup", "detonator"),
            FabricBlockEntityTypeBuilder.create(DetonatorBlockEntity::new,
                    ModBlocks.DETONATOR).build());

    public static void register() {
        // This method exists solely to force the class to load and its static fields to initialize.
        System.out.println("ModEntities registered");
    }
}
