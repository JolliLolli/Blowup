// ModBlocks.java
package name.blowup.registering;

import name.blowup.blocks.BlackHoleTNT;
import name.blowup.blocks.DetonatorBlock;
import name.blowup.blocks.Nuke;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;

import java.util.function.Function;

import static name.blowup.Blowup.LOGGER;
import static name.blowup.registering.Registering.*;

/**
 * This class is used to register custom blocks for the mod.
 * It contains static fields for each block and helper methods to initialise and register them.
 */
public class ModBlocks {

    /**
     * Register creative tab entries for blocks as items.
     */
    public static void initialise() {
        ItemGroupEvents.modifyEntriesEvent(BLOWUP_TAB_KEY)
                .register((entries) -> {
                    entries.add(NUKE.asItem());
                    entries.add(COMPRESSED_COBBLESTONE.asItem());
                    entries.add(BLACK_HOLE_TNT.asItem());
                    entries.add(ModItems.DETONATOR);
                });
    }

    /**
     * Registers a block into the Minecraft registry using a consistent structure.
     *
     * @param name              The name/id of the block.
     * @param factory           A factory method reference (typically a constructor) for the block.
     * @param settings          The settings for the block, including sound group, luminance, etc.
     * @param shouldRegisterItem Whether to register a BlockItem for this block.
     * @return The registered Block.
     */
    private static Block register(String name, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = factory.apply(settings.registryKey(blockKey));
        if (shouldRegisterItem)
            Registry.register(Registries.ITEM, keyOfItem(name), new BlockItem(block, new Item.Settings().registryKey(keyOfItem(name))));
        LOGGER.info("Registered item: {}", name);
        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    // Blocks
    public static final Block COMPRESSED_COBBLESTONE = register(
            "compressed_cobblestone",
            Block::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.STONE)
                    .luminance(state -> 1),
            true
    );

    public static final Block NUKE = register(
            "nuke",
            Nuke::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.GRASS),
            true
    );

    public static final Block BLACK_HOLE_TNT = register(
            "black_hole_tnt",
            BlackHoleTNT::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.GRASS),
            true
    );

    public static final Block DETONATOR = register(
            "detonator",
            DetonatorBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.STONE)
                    .luminance(state -> 1)
                    .nonOpaque()
                    .ticksRandomly(),
            false
    );
}
