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
    public static final BooleanProperty BOTTOM_NORTH = BooleanProperty.create("bottom_north");
    public static final BooleanProperty BOTTOM_EAST = BooleanProperty.create("bottom_east");
    public static final BooleanProperty BOTTOM_SOUTH = BooleanProperty.create("bottom_south");
    public static final BooleanProperty BOTTOM_WEST = BooleanProperty.create("bottom_west");
    public static final BooleanProperty NORTHEAST = BooleanProperty.create("northeast");
    public static final BooleanProperty NORTHWEST = BooleanProperty.create("northwest");
    public static final BooleanProperty SOUTHEAST = BooleanProperty.create("southeast");
    public static final BooleanProperty SOUTHWEST = BooleanProperty.create("southwest");
    public static final BooleanProperty TOP_NORTH = BooleanProperty.create("top_north");
    public static final BooleanProperty TOP_EAST = BooleanProperty.create("top_east");
    public static final BooleanProperty TOP_SOUTH = BooleanProperty.create("top_south");
    public static final BooleanProperty TOP_WEST = BooleanProperty.create("top_west");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final Map<Edge, BooleanProperty> EDGE_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Edge.class), (e) ->
    {
        e.put(Edge.BOTTOM_NORTH, BOTTOM_NORTH);
        e.put(Edge.BOTTOM_EAST, BOTTOM_EAST);
        e.put(Edge.BOTTOM_SOUTH, BOTTOM_SOUTH);
        e.put(Edge.BOTTOM_WEST, BOTTOM_WEST);
        e.put(Edge.NORTHEAST, NORTHEAST);
        e.put(Edge.NORTHWEST, NORTHWEST);
        e.put(Edge.SOUTHEAST, SOUTHEAST);
        e.put(Edge.SOUTHWEST, SOUTHWEST);
        e.put(Edge.TOP_NORTH, TOP_NORTH);
        e.put(Edge.TOP_EAST, TOP_EAST);
        e.put(Edge.TOP_SOUTH, TOP_SOUTH);
        e.put(Edge.TOP_WEST, TOP_WEST);
    });

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
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(BOTTOM_NORTH, Boolean.FALSE)
                .with(BOTTOM_EAST, Boolean.FALSE)
                .with(BOTTOM_SOUTH, Boolean.FALSE)
                .with(BOTTOM_WEST, Boolean.FALSE)
                .with(NORTHEAST, Boolean.FALSE)
                .with(NORTHWEST, Boolean.FALSE)
                .with(SOUTHEAST, Boolean.FALSE)
                .with(SOUTHWEST, Boolean.FALSE)
                .with(TOP_NORTH, Boolean.FALSE)
                .with(TOP_EAST, Boolean.FALSE)
                .with(TOP_SOUTH, Boolean.FALSE)
                .with(TOP_WEST, Boolean.FALSE)
                .with(WATERLOGGED, Boolean.FALSE));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape voxelshape = VoxelShapes.empty();
        if (state.get(BOTTOM_NORTH)) { voxelshape = VoxelShapes.or(voxelshape, BOTTOM_NORTH_AABB); }
        if (state.get(BOTTOM_EAST)) { voxelshape = VoxelShapes.or(voxelshape, BOTTOM_EAST_AABB); }
        if (state.get(BOTTOM_SOUTH)) { voxelshape = VoxelShapes.or(voxelshape, BOTTOM_SOUTH_AABB); }
        if (state.get(BOTTOM_WEST)) { voxelshape = VoxelShapes.or(voxelshape, BOTTOM_WEST_AABB); }
        if (state.get(NORTHEAST)) { voxelshape = VoxelShapes.or(voxelshape, NORTHEAST_AABB); }
        if (state.get(NORTHWEST)) { voxelshape = VoxelShapes.or(voxelshape, NORTHWEST_AABB); }
        if (state.get(SOUTHEAST)) { voxelshape = VoxelShapes.or(voxelshape, SOUTHEAST_AABB); }
        if (state.get(SOUTHWEST)) { voxelshape = VoxelShapes.or(voxelshape, SOUTHWEST_AABB); }
        if (state.get(TOP_NORTH)) { voxelshape = VoxelShapes.or(voxelshape, TOP_NORTH_AABB); }
        if (state.get(TOP_EAST)) { voxelshape = VoxelShapes.or(voxelshape, TOP_EAST_AABB); }
        if (state.get(TOP_SOUTH)) { voxelshape = VoxelShapes.or(voxelshape, TOP_SOUTH_AABB); }
        if (state.get(TOP_WEST)) { voxelshape = VoxelShapes.or(voxelshape, TOP_WEST_AABB); }
        return voxelshape;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getPos();
        BlockState blockState = context.getWorld().getBlockState(pos);
        Edge edge = Edge.getEdgeForContext(context);
        if (blockState.isIn(this))
        {
            // For adding to a existing block
            return blockState.with(EDGE_TO_PROPERTY_MAP.get(edge), true);
        }else {
            // For placing a new block:
            return this.getDefaultState().with(EDGE_TO_PROPERTY_MAP.get(edge), true);
        }
    }

    public boolean isReplaceable(BlockState blockState, BlockItemUseContext context)
    {
        if (context.getItem().getItem() == this.asItem())
        {
            if (context.getFace() == Direction.UP && context.getHitVec().y - context.getPos().getY() >= 1)
                return false;
            if (context.getFace() == Direction.DOWN && context.getHitVec().y - context.getPos().getY() <= 0)
                return false;
            if (context.getFace() == Direction.EAST && context.getHitVec().x - context.getPos().getX() >= 1)
                return false;
            if (context.getFace() == Direction.WEST && context.getHitVec().x - context.getPos().getX() <= 0 )
                return false;
            if (context.getFace() == Direction.SOUTH && context.getHitVec().z - context.getPos().getZ() >= 1)
                return false;
            if (context.getFace() == Direction.NORTH && context.getHitVec().z - context.getPos().getZ() <= 0)
                return false;

            Edge edge = Edge.getEdgeForContext(context);
            if (blockState.hasProperty(EDGE_TO_PROPERTY_MAP.get(edge)))
                return !blockState.get(EDGE_TO_PROPERTY_MAP.get(edge));
        }
        return false;
    }

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
        builder.add(BOTTOM_NORTH, BOTTOM_EAST, BOTTOM_SOUTH, BOTTOM_WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST, TOP_NORTH, TOP_EAST, TOP_SOUTH, TOP_WEST, WATERLOGGED);
    }
}
