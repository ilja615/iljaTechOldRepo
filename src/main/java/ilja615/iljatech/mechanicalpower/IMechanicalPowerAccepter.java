package ilja615.iljatech.mechanicalpower;

import ilja615.iljatech.init.ModProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IMechanicalPowerAccepter
{
    // If the block is able to receive power or not
    default boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom)
    {
        BlockState state = world.getBlockState(thisPos);
        return (state.hasProperty(ModProperties.MECHANICAL_POWER) && !((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning());
    }

    // What the block will do upon receiving power
    default void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        if (world.getBlockState(thisPos).hasProperty(ModProperties.MECHANICAL_POWER))
            world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.getFromAmount(amount)));
    };
}
