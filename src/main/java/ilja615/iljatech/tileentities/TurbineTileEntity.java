package ilja615.iljatech.tileentities;

import ilja615.iljatech.blocks.BellowsBlock;
import ilja615.iljatech.blocks.TurbineBlock;
import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModEntities;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.power.IMechanicalPowerAccepter;
import ilja615.iljatech.power.MechanicalPower;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class TurbineTileEntity extends TileEntity implements ITickableTileEntity
{
    public TurbineTileEntity(TileEntityType<?> tileEntityTypeIn) { super(tileEntityTypeIn); }
    public TurbineTileEntity() { this(ModTileEntityTypes.TURBINE.get()); }
    private int amountTicks;

    @Override
    public void tick()
    {
        if (this.level == null) return;
        if (this.level.isClientSide()) return;

        BlockState state = this.level.getBlockState(worldPosition);
        if (state.getBlock() != ModBlocks.TURBINE.get().getBlock()) return;

        if (this.amountTicks > 0)
        {
            --this.amountTicks;

            // If it's on, then every tick, the turbine will attempt output power to a neighbour
            ArrayList<Direction> directions = new ArrayList<Direction>(); // Potential directions that power could be outputted to.
            for (Direction dir : Direction.values()) {
                if (dir != state.getValue(DirectionalBlock.FACING)) // A turbine can not output to his "input" side.
                {
                    Block other = this.level.getBlockState(worldPosition.relative(dir)).getBlock();
                    if (other instanceof IMechanicalPowerAccepter && ((IMechanicalPowerAccepter)other).acceptsPower(this.level, worldPosition.relative(dir), dir.getOpposite()))
                        directions.add(dir);
                }
            }
            if (directions.size() > 0) {
                Direction randomlyPickedDirection = directions.get(this.level.random.nextInt(directions.size()));
                ((TurbineBlock)state.getBlock()).sendPower(this.level, worldPosition, randomlyPickedDirection);
            }
        }

        // The code for correctly updating the blockState of the TurbineBlock
        state = this.level.getBlockState(worldPosition); // Idk if this is needed a 2nd time but just in case for if it was changed in the meanwhile
        if (this.amountTicks > 5 && state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.SPINNING)
        {
            this.level.setBlockAndUpdate(worldPosition, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.SPINNING));
            System.out.println("started spinning");
        }
        else if (this.amountTicks == 5 && state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.ALMOST_STOPPING)
        {
            this.level.setBlockAndUpdate(worldPosition, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
            System.out.println("almost stopping spinning");
        }
        else if (this.amountTicks <= 0 && state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.OFF)
        {
            this.level.setBlockAndUpdate(worldPosition, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
            System.out.println("stopped spinning");
        }
    }

    public void startSpinning(World world, BlockPos pos, int amountTicksTime)
    {
        this.amountTicks = amountTicksTime;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        super.save(compound);
        compound.putInt("amountTicks", this.amountTicks);
        return compound;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound)
    {
        super.load(blockState, compound);

        this.amountTicks = compound.getInt("amountTicks");
    }
}
