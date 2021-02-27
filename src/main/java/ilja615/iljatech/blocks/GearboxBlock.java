package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.power.IMechanicalPowerAccepter;
import ilja615.iljatech.power.IMechanicalPowerSender;
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

public class GearboxBlock extends Block implements IMechanicalPowerAccepter, IMechanicalPowerSender
{
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final IntegerProperty ROTATION = IntegerProperty.create("mechanical_power", 0, 15);

    public GearboxBlock(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(ModProperties.MECHANICAL_POWER, 0));
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        super.tick(state, worldIn, pos, rand);
        if (state.getBlock() != this) {System.out.println("not this"); return;}

        int amount = state.get(ModProperties.MECHANICAL_POWER);
        System.out.println("Gearbox is now tick. He currently has " + amount + "power.");
        if (amount > 0)
        {
            ArrayList<Direction> directions = new ArrayList<Direction>();
            for (Direction dir : Direction.values()) {
                if (dir != state.get(FACING) && acceptsPower(worldIn, pos.offset(dir), dir.getOpposite(), amount))
                {
                    directions.add(dir);
                }
            }
            System.out.println("Gearbox has " + directions.size() + " sides where he can output power to.");
            if (directions.size() > 0) {
                Direction yes = directions.get(rand.nextInt(directions.size()));
                sendPower(worldIn, pos, yes, amount);
            } else {
                System.out.println("Gearbox has nowhere to send power to, voiding it.");
                worldIn.setBlockState(pos, state.with(ModProperties.MECHANICAL_POWER, 0));
            }
        }
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, int amount)
    {
        world.getPendingBlockTicks().scheduleTick(thisPos, this, 10);
        System.out.println("Tick scheduled");
        IMechanicalPowerAccepter.super.receivePower(world, thisPos, amount);
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        BlockState state = world.getBlockState(thisPos);
        return (state.hasProperty(FACING) && state.get(FACING) == sideFrom && state.hasProperty(ModProperties.MECHANICAL_POWER) && state.get(ModProperties.MECHANICAL_POWER) == 0);
    }

    @Override
    public boolean sendPower(World world, BlockPos thisPos, Direction face, int amount)
    {
        BlockState state = world.getBlockState(thisPos);
        if (IMechanicalPowerSender.super.sendPower(world, thisPos, face, amount))
        {
            world.setBlockState(thisPos, state.with(ModProperties.MECHANICAL_POWER, 0));
            return true;
        } else {
            return false;
        }
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(ModProperties.MECHANICAL_POWER, FACING);
    }
}
