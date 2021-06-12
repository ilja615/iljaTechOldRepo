package ilja615.iljatech.power;

import ilja615.iljatech.init.ModProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMechanicalPowerSender
{
    // The block will send power
    default boolean sendPower(World world, BlockPos thisPos, Direction face)
    {
        BlockPos neighborPos = thisPos.relative(face);
        if (world.getBlockState(neighborPos).getBlock() instanceof IMechanicalPowerAccepter) {
            if (((IMechanicalPowerAccepter) world.getBlockState(neighborPos).getBlock()).acceptsPower(world, neighborPos, face.getOpposite())) {
                ((IMechanicalPowerAccepter) world.getBlockState(neighborPos).getBlock()).receivePower(world, neighborPos, face.getOpposite());
                return true;
            }
        }
        return false;
    }
}