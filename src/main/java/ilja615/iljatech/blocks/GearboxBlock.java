package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.mechanicalpower.IMechanicalPowerAccepter;
import ilja615.iljatech.mechanicalpower.IMechanicalPowerSender;
import ilja615.iljatech.mechanicalpower.MechanicalPower;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.Random;

public class GearboxBlock extends Block implements IMechanicalPowerAccepter, IMechanicalPowerSender
{
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public GearboxBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand)
    {
        super.tick(state, worldIn, pos, rand);
        if (state.getBlock() != this) { return; }

        if (state.getValue(ModProperties.MECHANICAL_POWER) == MechanicalPower.ALMOST_STOPPING)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
        }
        else if (((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning())
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
                double power = ((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).getInt();
                if (Math.floor(power / ((double) directions.size())) > 0.0d)
                    directions.forEach(direction -> sendPower(worldIn, pos, direction, (int)(Math.floor(power / ((double) directions.size())))));
            } else {
                // There was nowhere to output to...
                worldIn.scheduleTick(pos, this, 5);
                worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
            }
        }
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        world.scheduleTick(thisPos, this, 10);
        IMechanicalPowerAccepter.super.receivePower(world, thisPos, sideFrom, amount);
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom)
    {
        BlockState state = world.getBlockState(thisPos);
        return (state.hasProperty(FACING) && state.getValue(FACING) == sideFrom && state.hasProperty(ModProperties.MECHANICAL_POWER) && !((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning());
    }

    @Override
    public boolean sendPower(Level world, BlockPos thisPos, Direction face, int amount)
    {
        BlockState state = world.getBlockState(thisPos);
        if (IMechanicalPowerSender.super.sendPower(world, thisPos, face, amount))
        {
            world.scheduleTick(thisPos, this, 5);
            world.setBlockAndUpdate(thisPos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
            return true;
        } else {
            return false;
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    public PushReaction getPistonPushReaction(BlockState state)
    {
        state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF);
        return PushReaction.NORMAL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(ModProperties.MECHANICAL_POWER, FACING);
    }
}
