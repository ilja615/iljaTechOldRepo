package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.power.IMechanicalPowerAccepter;
import ilja615.iljatech.power.IMechanicalPowerSender;
import ilja615.iljatech.power.MechanicalPower;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Interface;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

public class GearboxBlock extends Block implements IMechanicalPowerAccepter, IMechanicalPowerSender
{
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public GearboxBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        super.tick(state, worldIn, pos, rand);
        if (state.getBlock() != this) { return; }

        if (state.getValue(ModProperties.MECHANICAL_POWER) == MechanicalPower.ALMOST_STOPPING)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
        }
        else if (state.getValue(ModProperties.MECHANICAL_POWER) == MechanicalPower.SPINNING)
        {
            ArrayList<Direction> directions = new ArrayList<Direction>(); // Potential directions that power could be outputted to.
            for (Direction dir : Direction.values()) {
                if (dir != state.getValue(FACING)) // A gearbox can not output to his "input" side.
                {
                    Block other = worldIn.getBlockState(pos.relative(dir)).getBlock();
                    if (other instanceof IMechanicalPowerAccepter && ((IMechanicalPowerAccepter)other).acceptsPower(worldIn, pos.relative(dir), dir.getOpposite()))
                        directions.add(dir);
                }
            }
            if (directions.size() > 0) {
                Direction randomlyPickedDirection = directions.get(rand.nextInt(directions.size()));
                sendPower(worldIn, pos, randomlyPickedDirection);
            } else {
                // There was nowhere to output to...
                worldIn.getBlockTicks().scheduleTick(pos, this, 5);
                worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
            }
        }
    }

    @Override
    public void receivePower(World world, BlockPos thisPos)
    {
        world.getBlockTicks().scheduleTick(thisPos, this, 10);
        IMechanicalPowerAccepter.super.receivePower(world, thisPos);
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom)
    {
        BlockState state = world.getBlockState(thisPos);
        return (state.hasProperty(FACING) && state.getValue(FACING) == sideFrom && state.hasProperty(ModProperties.MECHANICAL_POWER) && state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.SPINNING);
    }

    @Override
    public boolean sendPower(World world, BlockPos thisPos, Direction face)
    {
        BlockState state = world.getBlockState(thisPos);
        if (IMechanicalPowerSender.super.sendPower(world, thisPos, face))
        {
            world.getBlockTicks().scheduleTick(thisPos, this, 5);
            world.setBlockAndUpdate(thisPos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
            return true;
        } else {
            return false;
        }
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(ModProperties.MECHANICAL_POWER, FACING);
    }
}
