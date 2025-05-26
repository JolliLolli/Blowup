package blowup.blocks;

import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

// Assume you have a CustomTNTEntity defined in your entities package
import blowup.entities.CustomTNTEntity;
import blowup.utils.ExplosionUtil;
import blowup.utils.Kaboom;

public abstract class CustomTNTBlock extends BlockTNT {

    public CustomTNTBlock() {
        // In 1.7.10, BlockTNT’s default constructor sets the material to tnt and adds the block to the redstone tab.
        super();
        // Optionally, set a custom block name or texture if you wish:
        this.setBlockName("customTNT");
        this.setBlockTextureName("blowup:custom_tnt");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float subX, float subY, float subZ) {
        ItemStack currentItem = player.getCurrentEquippedItem();
        if (currentItem != null && (currentItem.getItem() == Items.flint_and_steel || currentItem.getItem() == Items.fire_charge)) {
            primeTnt(world, x, y, z, player);
            world.setBlockToAir(x, y, z);
            if (currentItem.getItem() == Items.flint_and_steel) {
                currentItem.damageItem(1, player);
            } else {
                currentItem.stackSize--;
            }
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        if (!world.isRemote) {
            EntityLivingBase igniter = explosion.exploder instanceof EntityLivingBase ?
                (EntityLivingBase) explosion.exploder : null;
            primeChainReactionTntEntity(world, x, y, z, igniter);
        }
    }


    public void primeTnt(World world, int x, int y, int z, EntityLivingBase igniter) {
        if (!world.isRemote) {
            CustomTNTEntity entity = createCustomTNTEntity(world, x, y, z);
            Kaboom.giveTntHop(entity);
            world.spawnEntityInWorld(entity);
            world.playSoundAtEntity(entity, "game.tnt.primed", 1.0F, 1.0F);
        }
    }

    /**
     * Called when this TNT block is destroyed by an explosion to create a chain reaction.
     */
    public void primeChainReactionTntEntity(World world, int x, int y, int z, EntityLivingBase igniter) {
        CustomTNTEntity entity = createCustomTNTEntity(world, x, y, z);
        ExplosionUtil.primeChainReactionTntEntity(world, x, y, z, entity); // Adjust parameters as needed.
    }

    /**
     * Forces subclasses to implement how to create their specific TNT entity.
     */
    protected abstract CustomTNTEntity createCustomTNTEntity(World world, int x, int y, int z);
}
