package ilja615.iljatech.power;

import ilja615.iljatech.init.ModProperties;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMechanicalPowerAccepter
{
    // If the block is able to receive power or not
    default boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom)
    {
        BlockState state = world.getBlockState(thisPos);
        return (state.hasProperty(ModProperties.MECHANICAL_POWER) && !((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning());
    }

    // What the block will do upon receiving power
    default void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        if (world.getBlockState(thisPos).hasProperty(ModProperties.MECHANICAL_POWER))
            world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.getFromAmount(amount)));
    };
}
