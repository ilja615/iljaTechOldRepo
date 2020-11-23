package ilja615.iljatech.blocks;

import com.google.common.collect.Maps;
import ilja615.iljatech.util.Edge;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.Map;

public class RodBlock extends Block
{
    public static final EnumProperty<Edge> EDGE = EnumProperty.create("edge", Edge.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape BOTTOM_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D);
    private static final VoxelShape BOTTOM_EAST_AABB = Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape BOTTOM_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape BOTTOM_WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D);
    private static final VoxelShape NORTHEAST_AABB = Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape NORTHWEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
    private static final VoxelShape SOUTHEAST_AABB = Block.makeCuboidShape(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTHWEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
    private static final VoxelShape TOP_NORTH_AABB = Block.makeCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape TOP_EAST_AABB = Block.makeCuboidShape(14.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape TOP_SOUTH_AABB = Block.makeCuboidShape(0.0D, 14.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape TOP_WEST_AABB = Block.makeCuboidShape(0.0D, 14.0D, 0.0D, 2.0D, 16.0D, 16.0D);

    public RodBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(EDGE, Edge.BOTTOM_NORTH).with(WATERLOGGED, Boolean.FALSE));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape voxelshape = VoxelShapes.empty();
        if (state.get(EDGE) == Edge.BOTTOM_NORTH)  { voxelshape = VoxelShapes.or(voxelshape, BOTTOM_NORTH_AABB); }
        if (state.get(EDGE) == Edge.BOTTOM_EAST)   { voxelshape = VoxelShapes.or(voxelshape, BOTTOM_EAST_AABB); }
        if (state.get(EDGE) == Edge.BOTTOM_SOUTH)  { voxelshape = VoxelShapes.or(voxelshape, BOTTOM_SOUTH_AABB); }
        if (state.get(EDGE) == Edge.BOTTOM_WEST)   { voxelshape = VoxelShapes.or(voxelshape, BOTTOM_WEST_AABB); }
        if (state.get(EDGE) == Edge.NORTHEAST)     { voxelshape = VoxelShapes.or(voxelshape, NORTHEAST_AABB); }
        if (state.get(EDGE) == Edge.NORTHWEST)     { voxelshape = VoxelShapes.or(voxelshape, NORTHWEST_AABB); }
        if (state.get(EDGE) == Edge.SOUTHEAST)     { voxelshape = VoxelShapes.or(voxelshape, SOUTHEAST_AABB); }
        if (state.get(EDGE) == Edge.SOUTHWEST)     { voxelshape = VoxelShapes.or(voxelshape, SOUTHWEST_AABB); }
        if (state.get(EDGE) == Edge.TOP_NORTH)     { voxelshape = VoxelShapes.or(voxelshape, TOP_NORTH_AABB); }
        if (state.get(EDGE) == Edge.TOP_EAST)      { voxelshape = VoxelShapes.or(voxelshape, TOP_EAST_AABB); }
        if (state.get(EDGE) == Edge.TOP_SOUTH)     { voxelshape = VoxelShapes.or(voxelshape, TOP_SOUTH_AABB); }
        if (state.get(EDGE) == Edge.TOP_WEST)      { voxelshape = VoxelShapes.or(voxelshape, TOP_WEST_AABB); }
        return voxelshape;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(EDGE, Edge.getEdgeForContext(context));
    }

    public boolean isReplaceable(BlockState blockState, BlockItemUseContext context) { return false; }

    // Watterlogging stuff

    @Override
    public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        switch(p_196266_4_) {
            case LAND:
                return false;
            case WATER:
                return p_196266_2_.getFluidState(p_196266_3_).isTagged(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }

    public FluidState getFluidState(BlockState p_204507_1_) {
        return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
    }

    public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
        return true;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(EDGE, WATERLOGGED);
    }
}
