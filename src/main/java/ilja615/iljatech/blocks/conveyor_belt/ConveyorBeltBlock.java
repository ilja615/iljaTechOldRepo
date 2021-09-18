package ilja615.iljatech.blocks.conveyor_belt;

import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.mechanicalpower.IMechanicalPowerAccepter;
import ilja615.iljatech.mechanicalpower.MechanicalPower;
import ilja615.iljatech.blocks.conveyor_belt.ConveyorBeltBlockEntity;
import ilja615.iljatech.util.RotationDirection;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.Random;

import org.antlr.v4.runtime.misc.NotNull;

public class ConveyorBeltBlock extends BaseEntityBlock implements IMechanicalPowerAccepter
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_)
    {
        p_206840_1_.add(AXIS, ROTATION_DIRECTION, ModProperties.MECHANICAL_POWER);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        Direction direction = p_196258_1_.getNearestLookingDirection();
        return this.defaultBlockState()
                .setValue(AXIS, direction.getAxis())
                .setValue(ROTATION_DIRECTION, direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? RotationDirection.CLOCKWISE : RotationDirection.COUNTER_CLOCKWISE);
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
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
            worldIn.getBlockTicks().scheduleTick(pos, this, 10);
            worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
        }
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntityTypes.CONVEYOR_BELT.get(), ConveyorBeltBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return ModBlockEntityTypes.CONVEYOR_BELT.get().create(pos, state);
    }

    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState p_49232_) {
        return RenderShape.MODEL;
    }
}
