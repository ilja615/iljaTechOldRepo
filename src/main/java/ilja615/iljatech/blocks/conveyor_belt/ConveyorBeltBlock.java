package ilja615.iljatech.blocks.conveyor_belt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import ilja615.iljatech.energy.IMechanicalPowerSender;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.energy.IMechanicalPowerAccepter;
import ilja615.iljatech.energy.MechanicalPower;
import ilja615.iljatech.util.RotationDirection;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class ConveyorBeltBlock extends BaseEntityBlock implements IMechanicalPowerAccepter, IMechanicalPowerSender
{
    public static final EnumProperty AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty ROTATION_DIRECTION = EnumProperty.create("rotationdirection", RotationDirection.class);
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty ABOVE = BlockStateProperties.UP;
    public static final BooleanProperty BELOW = BlockStateProperties.DOWN;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), (p_55164_) -> {
        p_55164_.put(Direction.NORTH, NORTH);
        p_55164_.put(Direction.EAST, EAST);
        p_55164_.put(Direction.SOUTH, SOUTH);
        p_55164_.put(Direction.WEST, WEST);
        p_55164_.put(Direction.UP, ABOVE);
        p_55164_.put(Direction.DOWN, BELOW);
    }));

    public ConveyorBeltBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X).setValue(ROTATION_DIRECTION, RotationDirection.CLOCKWISE).setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_)
    {
        p_206840_1_.add(AXIS, ROTATION_DIRECTION, ModProperties.MECHANICAL_POWER, NORTH, EAST, SOUTH, WEST, ABOVE, BELOW);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockGetter blockGetter = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH, !blockGetter.getBlockState(pos.north()).getBlock().equals(this))
                .setValue(EAST, !blockGetter.getBlockState(pos.east()).getBlock().equals(this))
                .setValue(SOUTH, !blockGetter.getBlockState(pos.south()).getBlock().equals(this))
                .setValue(WEST, !blockGetter.getBlockState(pos.west()).getBlock().equals(this))
                .setValue(ABOVE, !blockGetter.getBlockState(pos.above()).getBlock().equals(this))
                .setValue(BELOW, !blockGetter.getBlockState(pos.below()).getBlock().equals(this))
                .setValue(AXIS, direction.getAxis())
                .setValue(ROTATION_DIRECTION, direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? RotationDirection.CLOCKWISE : RotationDirection.COUNTER_CLOCKWISE);
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
        return state.hasProperty(ModProperties.MECHANICAL_POWER) && !((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning();
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

    public PushReaction getPistonPushReaction(BlockState state)
    {
        state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF);
        return PushReaction.NORMAL;
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand)
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
                if (!state.getValue(PROPERTY_BY_DIRECTION.get(dir)).booleanValue()) // A conveyor can only output to sides where it is connected to.
                {
                    Block other = worldIn.getBlockState(pos.relative(dir)).getBlock();
                    if (other instanceof IMechanicalPowerAccepter && ((IMechanicalPowerAccepter)other).acceptsPower(worldIn, pos.relative(dir), dir.getOpposite()))
                        if (other.equals(this)) // Conveyor belt can only power another conveyor belt
                            directions.add(dir);
                }
            }
            if (directions.size() > 0) {
                int power = ((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).getInt();
                if (power > 1)
                    directions.forEach(direction -> sendPower(worldIn, pos, direction, power - 1));
                else {
                    // Insufficient power, could not output...
                    worldIn.scheduleTick(pos, this, 5);
                    worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
                }
            } else {
                // There was nowhere to output to...
                worldIn.scheduleTick(pos, this, 5);
                worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
            }
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

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState stateOther, LevelAccessor level, BlockPos pos, BlockPos posOther)
    {
        return state.setValue(PROPERTY_BY_DIRECTION.get(dir), !stateOther.getBlock().equals(this));
    }
}
