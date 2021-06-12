package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.util.RotationDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class ConveyorBeltBlock extends Block
{
    public static final EnumProperty AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty ROTATION_DIRECTION = EnumProperty.create("rotationdirection", RotationDirection.class);

    public ConveyorBeltBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(ROTATION_DIRECTION, RotationDirection.CLOCKWISE));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_)
    {
        p_206840_1_.add(AXIS, ROTATION_DIRECTION);
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        Direction direction = p_196258_1_.getNearestLookingDirection();
        return this.defaultBlockState()
                .setValue(AXIS, direction.getAxis())
                .setValue(ROTATION_DIRECTION, direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? RotationDirection.CLOCKWISE : RotationDirection.COUNTER_CLOCKWISE);
    }
}
