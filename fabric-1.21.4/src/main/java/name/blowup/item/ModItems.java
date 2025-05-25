package name.blowup.item;

import name.blowup.Blowup;
import name.blowup.blocks.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.function.Function;

/**
 * This class is used to register custom items for the mod.
 * It contains static fields for each item and a method to register them.
 * The items are registered with a unique identifier and can be accessed using their static fields.
 */
public class ModItems {
    // Tool Material
    public static final Logger LOGGER = LoggerFactory.getLogger(Blowup.MOD_ID);

    // 1) Generic register method
    private static <T extends Item> T register(String name, Function<Item.Settings, T> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Blowup.MOD_ID, name));
        // decorate the settings with registryKey(...)
        Item.Settings keyedSettings = settings.registryKey(key);
        T item = factory.apply(keyedSettings);
        Registry.register(Registries.ITEM, key, item);
        LOGGER.info("Registered item: {}", name);
        return item;
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

    // Custom creative tab
    public static final RegistryKey<ItemGroup> CUSTOM_TAB_KEY =
        RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Blowup.MOD_ID, "blowup"));
    public static final ItemGroup CUSTOM_TAB = FabricItemGroup.builder()
        .icon(() -> new ItemStack(ModBlocks.NUKE))
        .displayName(Text.translatable("itemGroup.Blowup"))
        .build();

    public static void initialise() {
        // Add to vanilla “Ingredients” tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
            .register(entries -> {
                entries.add(END_DUST);
                entries.add(BIG_SWORD);
            });

        // Register and add to custom tab
        Registry.register(Registries.ITEM_GROUP, CUSTOM_TAB_KEY, CUSTOM_TAB);
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_TAB_KEY)
            .register(entries -> {
                entries.add(END_DUST);
                entries.add(BIG_SWORD);
                entries.add(DETONATOR);
            });
    }
}