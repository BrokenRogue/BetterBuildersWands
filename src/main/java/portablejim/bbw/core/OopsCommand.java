package portablejim.bbw.core;

import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import portablejim.bbw.BetterBuildersWandsMod;
import portablejim.bbw.basics.Point3d;
import portablejim.bbw.core.items.IWandItem;
import portablejim.bbw.shims.BasicPlayerShim;

import java.util.ArrayList;

/**
 * /wandOops command.
 */
public class OopsCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "wandOops";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/wandOops";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {
        if(sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            ItemStack currentItemstack = BasicPlayerShim.getHeldWandIfAny(player);
            if(currentItemstack != null && currentItemstack.getItem() != null
                    && currentItemstack.getItem() instanceof IWandItem) {
                NBTTagCompound tagComponent = currentItemstack.getTagCompound();

                NBTTagCompound bbwCompound;
                if(tagComponent != null && tagComponent.hasKey("bbw", Constants.NBT.TAG_COMPOUND) && tagComponent.getCompoundTag("bbw").hasKey("lastPlaced", Constants.NBT.TAG_INT_ARRAY)) {
                    bbwCompound = tagComponent.getCompoundTag("bbw");
                    ArrayList<Point3d> pointList = unpackNbt(bbwCompound.getIntArray("lastPlaced"));
                    for (Point3d point : pointList) {
                        player.getEntityWorld().setBlockToAir(new BlockPos(point.x, point.y, point.z));
                    }
                    if(bbwCompound.hasKey("lastBlock", Constants.NBT.TAG_STRING) && bbwCompound.hasKey("lastPerBlock", Constants.NBT.TAG_INT) && bbwCompound.hasKey("lastBlockMeta")) {
                        String blockName= bbwCompound.getString("lastBlock");
                        int meta = bbwCompound.getInteger("lastBlockMeta");
                        ItemStack itemStack = GameRegistry.makeItemStack(blockName, meta, 1, "");
                        if(!itemStack.getHasSubtypes()) {
                            // If no subtypes, diffirent meta will mean an invalid block, so remove custom meta.
                            itemStack = GameRegistry.makeItemStack(blockName, 0, 1, "");
                        }
                        int count = bbwCompound.getInteger("lastPerBlock") * pointList.size();
                        int stackSize = itemStack.getMaxStackSize();
                        int fullStacks = count / stackSize;
                        if (!player.isCreative()) {
                            for (int i = 0; i < fullStacks; i++) {
                                ItemStack newStack = itemStack.copy();
                                newStack.stackSize = stackSize;
                                player.worldObj.spawnEntityInWorld(new EntityItem(player.getEntityWorld(), player.posX, player.posY, player.posZ, newStack));
                            }
                            ItemStack finalStack = itemStack.copy();
                            finalStack.stackSize = count % stackSize;
                            player.worldObj.spawnEntityInWorld(new EntityItem(player.getEntityWorld(), player.posX, player.posY, player.posZ, finalStack));
                        }

                        bbwCompound.removeTag("lastPlaced");
                        bbwCompound.removeTag("lastBlock");
                        bbwCompound.removeTag("lastBlockMeta");
                        bbwCompound.removeTag("lastPerBlock");
                    }
                }
                else {
                    throw new WrongUsageException(BetterBuildersWandsMod.LANGID + ".chat.error.noundo");
                }
            }
            else {
                throw new WrongUsageException(BetterBuildersWandsMod.LANGID + ".chat.error.nowand");
            }
        }
        else {
            throw new WrongUsageException(BetterBuildersWandsMod.LANGID + ".chat.error.bot");
        }
    }

    protected ArrayList<Point3d> unpackNbt(int[] placedBlocks) {
        ArrayList<Point3d> output = new ArrayList<Point3d>();
        int countPoints = placedBlocks.length / 3;
        for(int i = 0; i < countPoints * 3; i += 3) {
            output.add(new Point3d(placedBlocks[i], placedBlocks[i+1], placedBlocks[i+2]));
        }

        return output;
    }


    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
