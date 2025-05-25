package name.blowup.item;

import name.blowup.blocks.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;

import java.util.function.Function;

import static name.blowup.Blowup.LOGGER;
import static name.blowup.utils.Registering.*;

/**
 * This class is used to register custom items for the mod.
 * It contains static fields for each item and a method to register them.
 * The items are registered with a unique identifier and can be accessed using their static fields.
 */
public class ModItems {

    public static void initialise() {
        // Add to vanilla “Ingredients” tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register(entries -> {
                    entries.add(END_DUST);
                    entries.add(BIG_SWORD);
                });

        // Register and add to custom tab
        Registry.register(Registries.ITEM_GROUP, BLOWUP_TAB_KEY, BLOWUP_TAB);
        ItemGroupEvents.modifyEntriesEvent(BLOWUP_TAB_KEY)
                .register(entries -> {
                    entries.add(END_DUST);
                    entries.add(BIG_SWORD);
                    entries.add(DETONATOR);
                });
    }

    /**
     * Registers an item into the Minecraft registry using a consistent structure.
     *
     * @param name     The name/id of the item.
     * @param factory  A factory method reference (typically a constructor) for the item.
     * @param settings The settings for the item, including max stack size, group, etc.
     * @return The registered Item.
     */
    private static <T extends Item> T register(String name, Function<Item.Settings, T> factory, Item.Settings settings) {
        RegistryKey<Item> key = keyOfItem(name);
        T item = factory.apply(settings.registryKey(key));
        LOGGER.info("Registered item: {}", name);
        return Registry.register(Registries.ITEM, key, item);
    }

    public static final ToolMaterial BIG_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            455,
            5.0F,
            1.5F,
            22,
            ToolMaterial.DIAMOND.repairItems()
    );

    // Weapons
    public static final Item BIG_SWORD = register(
        "big_sword",
        s -> new BigSwordItem(BIG_MATERIAL, 5f, 1f, s.maxCount(1)),
        new Item.Settings()
    );

    // Other items
    public static final Item END_DUST = register(
        "end_dust",
        Item::new,
        new Item.Settings().maxCount(64)
    );

    // 3) Correct BlockItem registration, using the SAME settings
    public static final BlockItem DETONATOR = register(
        "detonator",
        s -> new BlockItem(ModBlocks.DETONATOR, s.maxCount(64)),
        new Item.Settings()
    );

    public static final Item DETONATOR_PARTICLE = register(
            "detonator_particle",
            Item::new,
            new Item.Settings()
    );
}