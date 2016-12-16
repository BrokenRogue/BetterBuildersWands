package portablejim.bbw.shims;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import portablejim.bbw.basics.Point3d;

import java.util.Random;

/**
 * Wrap functions to do with the world.
 */
public interface IWorldShim {
    Block getBlock(Point3d point);
    boolean blockIsAir(Point3d point);

    World getWorld();

    boolean copyBlock(Point3d originalBlock, Point3d blockPos);

    void setBlockToAir(Point3d blockPos);

    int getMetadata(Point3d blockPos);

    boolean entitiesInBox(AxisAlignedBB box);

    void playPlaceAtBlock(Point3d position, Block blockType);

    boolean setBlock(Point3d position, Block placeBlock, int placeMeta);

    boolean setBlock(Point3d blockPos, IBlockState targetBlock);

    Random rand();
}
