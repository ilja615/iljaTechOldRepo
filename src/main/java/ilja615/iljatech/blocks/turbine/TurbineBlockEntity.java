package ilja615.iljatech.blocks.turbine;

import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.mechanicalpower.IMechanicalPowerAccepter;
import ilja615.iljatech.mechanicalpower.MechanicalPower;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class TurbineBlockEntity extends BlockEntity
{
    private int amountTicks;

    public TurbineBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.TURBINE.get(), p_155229_, p_155230_);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TurbineBlockEntity blockEntity)
    {
        if (level == null) return;
        if (level.isClientSide()) return;

        if (state.getBlock() != ModBlocks.TURBINE.get()) return;

        if (blockEntity.amountTicks > 0)
        {
            --blockEntity.amountTicks;

            // If it's on, then every tick, the turbine will attempt output power to a neighbour
            ArrayList<Direction> directions = new ArrayList<Direction>(); // Potential directions that power could be outputted to.
            for (Direction dir : Direction.values()) {
                if (dir != state.getValue(DirectionalBlock.FACING)) // A turbine can not output to his "input" side.
                {
                    Block other = level.getBlockState(pos.relative(dir)).getBlock();
                    if (other instanceof IMechanicalPowerAccepter && ((IMechanicalPowerAccepter)other).acceptsPower(level, pos.relative(dir), dir.getOpposite()))
                        directions.add(dir);
                }
            }
            if (directions.size() > 0) {
                Direction randomlyPickedDirection = directions.get(level.random.nextInt(directions.size()));
                ((TurbineBlock)state.getBlock()).sendPower(level, pos, randomlyPickedDirection, 8);
            }
        }

        // The code for correctly updating the blockState of the TurbineBlock
        state = level.getBlockState(pos); // Idk if this is needed a 2nd time but just in case for if it was changed in the meanwhile
        if (blockEntity.amountTicks > 5 && !((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning())
        {
            level.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.SPINNING_8));
        }
        else if (blockEntity.amountTicks == 5 && state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.ALMOST_STOPPING)
        {
            level.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
        }
        else if (blockEntity.amountTicks <= 0 && state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.OFF)
        {
            level.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
        }
    }

    public void startSpinning(Level world, BlockPos pos, int amountTicksTime)
    {
        this.amountTicks = amountTicksTime;
    }

    @Override
    public void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        compound.putInt("amountTicks", this.amountTicks);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);

        this.amountTicks = compound.getInt("amountTicks");
    }
}
