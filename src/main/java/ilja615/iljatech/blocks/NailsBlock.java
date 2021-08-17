package ilja615.iljatech.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class NailsBlock extends Block
{
    public static final DirectionProperty FACE = BlockStateProperties.FACING;
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);

    protected static final VoxelShape UP_BIG_AABB = Block.box(1.0D, 10.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape DOWN_BIG_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 6.0D, 15.0D);
    protected static final VoxelShape NORTH_BIG_AABB = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 6.0D);
    protected static final VoxelShape SOUTH_BIG_AABB = Block.box(1.0D, 1.0D, 10.0D, 15.0D, 15.0D, 16.0D);
    protected static final VoxelShape WEST_BIG_AABB = Block.box(0.0D, 1.0D, 1.0D, 6.0D, 15.0D, 15.0D);
    protected static final VoxelShape EAST_BIG_AABB = Block.box(10.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    protected static final VoxelShape UP_SMALL_AABB = Block.box(1.0D, 13.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape DOWN_SMALL_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 3.0D, 15.0D);
    protected static final VoxelShape NORTH_SMALL_AABB = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 3.0D);
    protected static final VoxelShape SOUTH_SMALL_AABB = Block.box(1.0D, 1.0D, 13.0D, 15.0D, 15.0D, 16.0D);
    protected static final VoxelShape WEST_SMALL_AABB = Block.box(0.0D, 1.0D, 1.0D, 3.0D, 15.0D, 15.0D);
    protected static final VoxelShape EAST_SMALL_AABB = Block.box(13.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);

    public NailsBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0).setValue(FACE, Direction.DOWN));
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        if (state.getValue(STAGE) == 0 || state.getValue(STAGE) == 1)
        {
            switch((Direction)state.getValue(FACE)) {
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
            switch((Direction)state.getValue(FACE)) {
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

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        return FaceAttachedHorizontalDirectionalBlock.canAttach(worldIn, pos, state.getValue(FACE));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACE, context.getClickedFace().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(STAGE, FACE);
    }
}
