package ilja615.iljatech.power;

import ilja615.iljatech.init.ModProperties;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMechanicalPowerAccepter
{
    default boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom, int amount) { return true; }
    default void receivePower(World world, BlockPos thisPos, int amount)
    {
        if (world.isRemote) {
            System.out.println("Received " + amount + " power at coords: " + thisPos.getCoordinatesAsString());
            world.setBlockState(thisPos, world.getBlockState(thisPos).with(ModProperties.MECHANICAL_POWER, amount));
        }
    };
}
