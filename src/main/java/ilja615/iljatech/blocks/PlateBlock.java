package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class PlateBlock extends Block implements SimpleWaterloggedBlock
{
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = PipeBlock.PROPERTY_BY_DIRECTION;

    protected static final VoxelShape UP_AABB = Block.box(0.0d, 14.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    protected static final VoxelShape DOWN_AABB = Block.box(0.0d, 0.0d, 0.0d, 16.0d, 2.0d, 16.0d);
    protected static final VoxelShape WEST_AABB = Block.box(0.0d, 0.0d, 0.0d, 2.0d, 16.0d, 16.0d);
    protected static final VoxelShape EAST_AABB = Block.box(14.0d, 0.0d, 0.0d, 16.0d, 16.0d, 16.0d);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0d, 0.0d, 0.0d, 16.0d, 16.0d, 2.0d);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0d, 0.0d, 14.0d, 16.0d, 16.0d, 16.0d);

    public PlateBlock(Properties p_i48440_1_)
    {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(UP, Boolean.valueOf(false)).setValue(DOWN, Boolean.valueOf(false)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape voxelshape = Shapes.empty();
        if (state.getValue(UP)) {
            voxelshape = Shapes.or(voxelshape, UP_AABB);
        }

        if (state.getValue(DOWN)) {
            voxelshape = Shapes.or(voxelshape, DOWN_AABB);
        }

        if (state.getValue(NORTH)) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }

        if (state.getValue(EAST)) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }

        if (state.getValue(SOUTH)) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }

        if (state.getValue(WEST)) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }
        return voxelshape;
    }

    public static BooleanProperty getPropertyFor(Direction side) {
        return FACING_TO_PROPERTY_MAP.get(side);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockPos pos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(pos);
        if (blockState.is(this))
        {
            // For adding to a existing block
            if (context.getClickedFace().getAxis() == Direction.Axis.Y)
            {
                if (context.getClickLocation().x - context.getClickedPos().getX() > 0.875D)
                    return blockState.setValue(EAST, true);
                if (context.getClickLocation().x - context.getClickedPos().getX() < 0.125D)
                    return blockState.setValue(WEST, true);
                if (context.getClickLocation().z - context.getClickedPos().getZ() > 0.875D)
                    return blockState.setValue(SOUTH, true);
                if (context.getClickLocation().z - context.getClickedPos().getZ() < 0.125D)
                    return blockState.setValue(NORTH, true);
            }
            if (context.getClickedFace().getAxis() == Direction.Axis.X)
            {
                if (context.getClickLocation().y - context.getClickedPos().getY() > 0.875D)
                    return blockState.setValue(UP, true);
                if (context.getClickLocation().y - context.getClickedPos().getY() < 0.125D)
                    return blockState.setValue(DOWN, true);
                if (context.getClickLocation().z - context.getClickedPos().getZ() > 0.875D)
                    return blockState.setValue(SOUTH, true);
                if (context.getClickLocation().z - context.getClickedPos().getZ() < 0.125D)
                    return blockState.setValue(NORTH, true);
            }
            if (context.getClickedFace().getAxis() == Direction.Axis.Z)
            {
                if (context.getClickLocation().y - context.getClickedPos().getY() > 0.875D)
                    return blockState.setValue(UP, true);
                if (context.getClickLocation().y - context.getClickedPos().getY() < 0.125D)
                    return blockState.setValue(DOWN, true);
                if (context.getClickLocation().x - context.getClickedPos().getX() > 0.875D)
                    return blockState.setValue(EAST, true);
                if (context.getClickLocation().x - context.getClickedPos().getX() < 0.125D)
                    return blockState.setValue(WEST, true);
            }
            // Default:
            if (!blockState.getValue(FACING_TO_PROPERTY_MAP.get(context.getClickedFace().getOpposite())))
                return blockState.setValue(FACING_TO_PROPERTY_MAP.get(context.getClickedFace().getOpposite()), true);
            else
                return blockState;
        } else {
            // For placing a new block:
            if (context.getClickedFace().getAxis() == Direction.Axis.Y)
            {
                if (context.getClickLocation().x - context.getClickedPos().getX() > 0.875D)
                    return this.defaultBlockState().setValue(EAST, true);
                if (context.getClickLocation().x - context.getClickedPos().getX() < 0.125D)
                    return this.defaultBlockState().setValue(WEST, true);
                if (context.getClickLocation().z - context.getClickedPos().getZ() > 0.875D)
                    return this.defaultBlockState().setValue(SOUTH, true);
                if (context.getClickLocation().z - context.getClickedPos().getZ() < 0.125D)
                    return this.defaultBlockState().setValue(NORTH, true);
                return this.defaultBlockState().setValue(FACING_TO_PROPERTY_MAP.get(context.getClickedFace().getOpposite()), true);
            }
            if (context.getClickedFace().getAxis() == Direction.Axis.X)
            {
                if (context.getClickLocation().y - context.getClickedPos().getY() > 0.875D)
                    return this.defaultBlockState().setValue(UP, true);
                if (context.getClickLocation().y - context.getClickedPos().getY() < 0.125D)
                    return this.defaultBlockState().setValue(DOWN, true);
                if (context.getClickLocation().z - context.getClickedPos().getZ() > 0.875D)
                    return this.defaultBlockState().setValue(SOUTH, true);
                if (context.getClickLocation().z - context.getClickedPos().getZ() < 0.125D)
                    return this.defaultBlockState().setValue(NORTH, true);
                return this.defaultBlockState().setValue(FACING_TO_PROPERTY_MAP.get(context.getClickedFace().getOpposite()), true);
            }
            if (context.getClickedFace().getAxis() == Direction.Axis.Z)
            {
                if (context.getClickLocation().y - context.getClickedPos().getY() > 0.875D)
                    return this.defaultBlockState().setValue(UP, true);
                if (context.getClickLocation().y - context.getClickedPos().getY() < 0.125D)
                    return this.defaultBlockState().setValue(DOWN, true);
                if (context.getClickLocation().x - context.getClickedPos().getX() > 0.875D)
                    return this.defaultBlockState().setValue(EAST, true);
                if (context.getClickLocation().x - context.getClickedPos().getX() < 0.125D)
                    return this.defaultBlockState().setValue(WEST, true);
                return this.defaultBlockState().setValue(FACING_TO_PROPERTY_MAP.get(context.getClickedFace().getOpposite()), true);
            }
            return this.defaultBlockState().setValue(FACING_TO_PROPERTY_MAP.get(context.getClickedFace()), true);
        }
    }

    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context)
    {
        if (context.getItemInHand().getItem() == this.asItem())
        {
            if (context.getClickedFace() == Direction.UP && context.getClickLocation().y - context.getClickedPos().getY() >= 1)
                return false;
            if (context.getClickedFace() == Direction.DOWN && context.getClickLocation().y - context.getClickedPos().getY() <= 0)
                return false;
            if (context.getClickedFace() == Direction.EAST && context.getClickLocation().x - context.getClickedPos().getX() >= 1)
                return false;
            if (context.getClickedFace() == Direction.WEST && context.getClickLocation().x - context.getClickedPos().getX() <= 0)
                return false;
            if (context.getClickedFace() == Direction.SOUTH && context.getClickLocation().z - context.getClickedPos().getZ() >= 1)
                return false;
            if (context.getClickedFace() == Direction.NORTH && context.getClickLocation().z - context.getClickedPos().getZ() <= 0)
                return false;
            return true;
        }
        return false;
    }

    // Watterlogging stuff

    @Override
    public boolean isPathfindable(BlockState p_196266_1_, BlockGetter p_196266_2_, BlockPos p_196266_3_, PathComputationType p_196266_4_) {
        switch(p_196266_4_) {
            case LAND:
                return false;
            case WATER:
                return p_196266_2_.getFluidState(p_196266_3_).is(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }

    public FluidState getFluidState(BlockState p_204507_1_) {
        return (Boolean)p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
    }

    public boolean canPlaceLiquid(BlockGetter p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
        return true;
    }

    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, LevelAccessor p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if ((Boolean)p_196271_1_.getValue(WATERLOGGED)) {
            p_196271_4_.scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
        }

        return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
    }
}
