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


import java.util.function.Function;

/**
 * This class is used to register custom items for the mod.
 * It contains static fields for each item and a method to register them.
 * The items are registered with a unique identifier and can be accessed using their static fields.
 */
public class ModItems {
    // Tool Material
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
            settings -> new BigSwordItem(BIG_MATERIAL, 5f, 1f, settings),
            new Item.Settings());

    // Other items
    public static final Item END_DUST = register("end_dust", Item::new, new Item.Settings().maxCount(64));

    // Custom item group called Blowup so you can find the items in the creative inventory
    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Blowup.MOD_ID, "Blowup"));
    public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.NUKE))
            .displayName(Text.translatable("itemGroup.Blowup"))
            .build();

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Blowup.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    // Register the items here
    public static void initialise() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register(entries -> {
                    entries.add(ModItems.END_DUST);
                    entries.add(ModItems.BIG_SWORD);
                });
        // Register the group.
        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);

        // Register items to the custom item group.
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY)
                .register(itemGroup -> {
                    itemGroup.add(ModItems.BIG_SWORD);
                    itemGroup.add(ModItems.END_DUST);
                });

    }
}