package name.blowup.blocks;

import name.blowup.Blowup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import name.blowup.item.ModItems;

import java.util.function.Function;

public class ModBlocks {
    public static final Block NUKE = register(
            "nuke",
            Nuke::new,
            AbstractBlock.Settings.create().sounds(BlockSoundGroup.GRASS),
            true
    );

    public static final Block COMPRESSED_COBBLESTONE = register(
            "compressed_cobblestone",
            Block::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.STONE)
                    .luminance(state -> 1),
            true
    );

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        // Create a registry key for the block
        RegistryKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    public static void initialize() {
        // This method is called to register all blocks.
        // You can also register items here, but it's better to keep them separate.
        // This is just a simple example of how to register a block.
        // You can add more blocks here as needed.
        ItemGroupEvents.modifyEntriesEvent(ModItems.CUSTOM_ITEM_GROUP_KEY).register((itemGroup) -> {
            itemGroup.add(ModBlocks.NUKE.asItem());
            itemGroup.add(ModBlocks.COMPRESSED_COBBLESTONE.asItem());
        });
    }
    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Blowup.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Blowup.MOD_ID, name));
    }

}