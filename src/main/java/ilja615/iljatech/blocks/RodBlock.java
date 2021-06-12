package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.power.IMechanicalPowerAccepter;
import ilja615.iljatech.power.IMechanicalPowerSender;
import ilja615.iljatech.power.MechanicalPower;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class RodBlock extends DirectionalBlock implements IMechanicalPowerAccepter, IMechanicalPowerSender
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty INPUT_KEY = BooleanProperty.create("last_input_axis_direction");

    protected static final VoxelShape Y_AXIS_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    public RodBlock(AbstractBlock.Properties p_i48404_1_) {
        super(p_i48404_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false).setValue(INPUT_KEY, false));
    }

    public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.setValue(FACING, p_185471_2_.mirror(p_185471_1_.getValue(FACING)));
    }

    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        switch(p_220053_1_.getValue(FACING).getAxis()) {
            case X:
            default:
                return X_AXIS_AABB;
            case Z:
                return Z_AXIS_AABB;
            case Y:
                return Y_AXIS_AABB;
        }
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        Direction direction = p_196258_1_.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(FACING, WATERLOGGED, ModProperties.MECHANICAL_POWER, INPUT_KEY);
    }

    public FluidState getFluidState(BlockState p_204507_1_) {
        return (Boolean)p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
    }

    public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
        return true;
    }

    public PushReaction getPistonPushReaction(BlockState state)
    {
        state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF);
        return PushReaction.NORMAL;
    }

    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        return false;
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
            Direction.AxisDirection lastReceivedPower = state.getValue(INPUT_KEY) ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
            Direction dir = state.getValue(FACING);
            Block other = worldIn.getBlockState(pos.relative(dir)).getBlock();
            if (dir.getAxisDirection() != lastReceivedPower && other instanceof IMechanicalPowerAccepter && ((IMechanicalPowerAccepter)other).acceptsPower(worldIn, pos.relative(dir), dir.getOpposite()))
                sendPower(worldIn, pos, dir);
            else
            {
                dir = state.getValue(FACING).getOpposite();
                if (dir.getAxisDirection() != lastReceivedPower && other instanceof IMechanicalPowerAccepter && ((IMechanicalPowerAccepter)other).acceptsPower(worldIn, pos.relative(dir), dir.getOpposite()))
                    sendPower(worldIn, pos, dir);
                else
                {
                     // There was nowhere to output to...
                    worldIn.getBlockTicks().scheduleTick(pos, this, 5);
                    worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
                }
            }
        }
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom)
    {
        world.setBlockAndUpdate(thisPos, world.getBlockState(thisPos).setValue(INPUT_KEY, sideFrom.getAxisDirection() == Direction.AxisDirection.POSITIVE ? true : false));
        world.getBlockTicks().scheduleTick(thisPos, this, 10);
        IMechanicalPowerAccepter.super.receivePower(world, thisPos, sideFrom);
    }

    @Override
    public boolean acceptsPower(World world, BlockPos thisPos, Direction sideFrom)
    {
        BlockState state = world.getBlockState(thisPos);
        return (state.hasProperty(FACING) && (state.getValue(FACING) == sideFrom || state.getValue(FACING).getOpposite() == sideFrom) && state.hasProperty(ModProperties.MECHANICAL_POWER) && state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.SPINNING);
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

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_)
    {
        if (state.hasProperty(ModProperties.MECHANICAL_POWER) && (state.getValue(ModProperties.MECHANICAL_POWER) == MechanicalPower.SPINNING || state.getValue(ModProperties.MECHANICAL_POWER) == MechanicalPower.ALMOST_STOPPING))
        {
            double d0 = (double)p_180655_3_.getX() + 0.25D + (0.5f * p_180655_4_.nextDouble());
            double d1 = (double)p_180655_3_.getY() + 0.25D + (0.5f * p_180655_4_.nextDouble());
            double d2 = (double)p_180655_3_.getZ() + 0.25D + (0.5f * p_180655_4_.nextDouble());
            p_180655_2_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}