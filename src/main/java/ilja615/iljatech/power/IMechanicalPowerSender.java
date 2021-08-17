package ilja615.iljatech.power;

import ilja615.iljatech.init.ModProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IMechanicalPowerSender
{
    // The block will send power
    default boolean sendPower(Level world, BlockPos thisPos, Direction face, int amount)
    {
        BlockPos neighborPos = thisPos.relative(face);
        if (world.getBlockState(neighborPos).getBlock() instanceof IMechanicalPowerAccepter) {
            if (((IMechanicalPowerAccepter) world.getBlockState(neighborPos).getBlock()).acceptsPower(world, neighborPos, face.getOpposite())) {
                ((IMechanicalPowerAccepter) world.getBlockState(neighborPos).getBlock()).receivePower(world, neighborPos, face.getOpposite(), amount);
                return true;
            }
        }
        return false;
    }
}