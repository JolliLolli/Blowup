package name.blowup.registering;

import name.blowup.Blowup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Registering {
    public static final RegistryKey<ItemGroup> BLOWUP_TAB_KEY =
            RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Blowup.MOD_ID, "blowup"));
    public static final ItemGroup BLOWUP_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.NUKE))
            .displayName(Text.translatable("itemGroup.Blowup"))
            .build();

    public static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Blowup.MOD_ID, name));
    }

    public static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Blowup.MOD_ID, name));
    }

    public static RegistryKey<EntityType<?>> keyOfEntity(String name) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Blowup.MOD_ID, name));
    }

    public static RegistryKey<BlockEntityType<?>> keyOfBlockEntity(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK_ENTITY_TYPE, Identifier.of(Blowup.MOD_ID, name));
    }
}
