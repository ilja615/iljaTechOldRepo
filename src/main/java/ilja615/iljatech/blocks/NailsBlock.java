package ilja615.iljatech.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import ilja615.iljatech.util.Edge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class NailsBlock extends Block
{
    public static final DirectionProperty FACE = BlockStateProperties.FACING;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);

    protected static final VoxelShape UP_BIG_AABB = Block.makeCuboidShape(1.0D, 10.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape DOWN_BIG_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 6.0D, 15.0D);
    protected static final VoxelShape NORTH_BIG_AABB = Block.makeCuboidShape(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 6.0D);
    protected static final VoxelShape SOUTH_BIG_AABB = Block.makeCuboidShape(1.0D, 1.0D, 10.0D, 15.0D, 15.0D, 16.0D);
    protected static final VoxelShape WEST_BIG_AABB = Block.makeCuboidShape(0.0D, 1.0D, 1.0D, 6.0D, 15.0D, 15.0D);
    protected static final VoxelShape EAST_BIG_AABB = Block.makeCuboidShape(10.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    protected static final VoxelShape UP_SMALL_AABB = Block.makeCuboidShape(1.0D, 13.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape DOWN_SMALL_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 3.0D, 15.0D);
    protected static final VoxelShape NORTH_SMALL_AABB = Block.makeCuboidShape(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 3.0D);
    protected static final VoxelShape SOUTH_SMALL_AABB = Block.makeCuboidShape(1.0D, 1.0D, 13.0D, 15.0D, 15.0D, 16.0D);
    protected static final VoxelShape WEST_SMALL_AABB = Block.makeCuboidShape(0.0D, 1.0D, 1.0D, 3.0D, 15.0D, 15.0D);
    protected static final VoxelShape EAST_SMALL_AABB = Block.makeCuboidShape(13.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);

    public NailsBlock(Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(STAGE, 0).with(FACE, Direction.DOWN));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (state.get(STAGE) == 0 || state.get(STAGE) == 1)
        {
            switch((Direction)state.get(FACE)) {
                case DOWN:
                default:
                    return DOWN_BIG_AABB;
                case UP:
                    return UP_BIG_AABB;
                case NORTH:
                    return NORTH_BIG_AABB;
                case SOUTH:
                    return SOUTH_BIG_AABB;
                case WEST:
                    return WEST_BIG_AABB;
                case EAST:
                    return EAST_BIG_AABB;
            }
        } else
        {
            switch((Direction)state.get(FACE)) {
                case DOWN:
                default:
                    return DOWN_SMALL_AABB;
                case UP:
                    return UP_SMALL_AABB;
                case NORTH:
                    return NORTH_SMALL_AABB;
                case SOUTH:
                    return SOUTH_SMALL_AABB;
                case WEST:
                    return WEST_SMALL_AABB;
                case EAST:
                    return EAST_SMALL_AABB;
            }
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACE, context.getFace().getOpposite());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(STAGE, FACE);
    }
}
