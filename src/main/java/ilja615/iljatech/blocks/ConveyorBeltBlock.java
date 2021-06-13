package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.power.IMechanicalPowerAccepter;
import ilja615.iljatech.power.MechanicalPower;
import ilja615.iljatech.util.RotationDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class ConveyorBeltBlock extends Block implements IMechanicalPowerAccepter
{
    public static final EnumProperty AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty ROTATION_DIRECTION = EnumProperty.create("rotationdirection", RotationDirection.class);

//    protected static final VoxelShape Y_AXIS_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
//    protected static final VoxelShape Z_AXIS_AABB = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 16.0D);
//    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);

    public ConveyorBeltBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(ROTATION_DIRECTION, RotationDirection.CLOCKWISE).setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
    }

//    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
//        switch((Direction.Axis)p_220053_1_.getValue(AXIS)) {
//            case X:
//            default:
//                return X_AXIS_AABB;
//            case Z:
//                return Z_AXIS_AABB;
//            case Y:
//                return Y_AXIS_AABB;
//        }
//    }
//
//    public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos) {
//        return VoxelShapes.block();
//    }
//
//    public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
//        return VoxelShapes.block();
//    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_)
    {
        p_206840_1_.add(AXIS, ROTATION_DIRECTION, ModProperties.MECHANICAL_POWER);
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        Direction direction = p_196258_1_.getNearestLookingDirection();
        return this.defaultBlockState()
                .setValue(AXIS, direction.getAxis())
                .setValue(ROTATION_DIRECTION, direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? RotationDirection.CLOCKWISE : RotationDirection.COUNTER_CLOCKWISE);
    }

    @Override
    public void receivePower(World world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        world.getBlockTicks().scheduleTick(thisPos, this, 100);
        IMechanicalPowerAccepter.super.receivePower(world, thisPos, sideFrom, amount);
    }

    public PushReaction getPistonPushReaction(BlockState state)
    {
        state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF);
        return PushReaction.NORMAL;
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
        else if (((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning())
        {
            worldIn.getBlockTicks().scheduleTick(pos, this, 10);
            worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return ModTileEntityTypes.CONVEYOR_BELT.get().create();
    }
}
