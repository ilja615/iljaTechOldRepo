package ilja615.iljatech.power;

import ilja615.iljatech.init.ModProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMechanicalPowerSender
{
    default boolean sendPower(World world, BlockPos thisPos, Direction face, int amount)
    {
        BlockPos neighborPos = thisPos.offset(face);
        if (world.isRemote) {
            if (world.getBlockState(neighborPos).getBlock() instanceof IMechanicalPowerAccepter) {
                if (((IMechanicalPowerAccepter) world.getBlockState(neighborPos).getBlock()).acceptsPower(world, neighborPos, face.getOpposite(), amount)) {
                    System.out.println(world.getBlockState(thisPos).getBlock().toString() + "is attempting to send " + amount + " power to: " + world.getBlockState(neighborPos).toString() + " at coords:" + neighborPos.getCoordinatesAsString());
                    ((IMechanicalPowerAccepter) world.getBlockState(neighborPos).getBlock()).receivePower(world, neighborPos, amount);
                    return true;
                }
            }
            System.out.println(amount + " power could not be sent by " + world.getBlockState(thisPos).getBlock().toString());
        }
        return false;
    }
}