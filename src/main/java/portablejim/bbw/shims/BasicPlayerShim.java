package portablejim.bbw.shims;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import portablejim.bbw.basics.Point3d;
import portablejim.bbw.core.items.IWandItem;

/**
 * Wrap a player to provide basic functions.
 */
public class BasicPlayerShim implements IPlayerShim {
    private EntityPlayer player;

    public BasicPlayerShim(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public int countItems(ItemStack itemStack) {
        int total = 0;
        if(itemStack == null || player.inventory == null || player.inventory.mainInventory == null) {
            return 0;
        }


        for(ItemStack inventoryStack : player.inventory.mainInventory) {
            if(inventoryStack != null && itemStack.isItemEqual(inventoryStack)) {
                total += Math.max(0, inventoryStack.getCount());
            }
        }

        return itemStack.getCount() > 0 ? total / itemStack.getCount() : 0;
    }

    @Override
    public boolean useItem(ItemStack itemStack) {
        if(itemStack == null || player.inventory == null || player.inventory.mainInventory == null) {
            return false;
        }

        // Reverse direction to leave hotbar to last.
        int toUse = itemStack.getCount();
        for(int i = player.inventory.mainInventory.size()- 1; i >= 0; i--) {
            ItemStack inventoryStack = player.inventory.mainInventory.get(i);
            if(inventoryStack != null && itemStack.isItemEqual(inventoryStack)) {
                if(inventoryStack.getCount() < toUse) {
                    inventoryStack.setCount(0);
                    toUse -= inventoryStack.getCount();
                }
                else {
                    inventoryStack.setCount(inventoryStack.getCount() - toUse);
                    toUse = 0;
                }
                if(inventoryStack.getCount() == 0) {
                    player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                }
                player.inventoryContainer.detectAndSendChanges();
                if(toUse <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack getNextItem(Block block, int meta) {
        for(int i = player.inventory.mainInventory.size() - 1; i >= 0; i--) {
            ItemStack inventoryStack = player.inventory.mainInventory.get(i);

        }

        return null;
    }

    @Override
    public Point3d getPlayerPosition() {
        return new Point3d((int)player.posX, (int)player.posY, (int)player.posZ);
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public ItemStack getHeldWandIfAny() {
        return getHeldWandIfAny(player);
    }

    public static ItemStack getHeldWandIfAny(EntityPlayer player) {
        ItemStack wandItem = null;
        if(player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof IWandItem) {
            wandItem = player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else if(player.getHeldItem(EnumHand.OFF_HAND) != null && player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof IWandItem) {
            wandItem = player.getHeldItem(EnumHand.OFF_HAND);
        }
        return wandItem;
    }

    @Override
    public boolean isCreative() {
        return player.capabilities.isCreativeMode;
    }
}
