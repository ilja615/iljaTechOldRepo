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
    public static final BooleanProperty NORTHEAST = BooleanProperty.create("northeast");
    public static final BooleanProperty NORTHWEST = BooleanProperty.create("northwest");
    public static final BooleanProperty SOUTHEAST = BooleanProperty.create("southeast");
    public static final BooleanProperty SOUTHWEST = BooleanProperty.create("southwest");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final Map<Edge, BooleanProperty> EDGE_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Edge.class), (edgeBooleanPropertyEnumMap) -> {
        edgeBooleanPropertyEnumMap.put(Edge.NORTHEAST, NORTHEAST);
        edgeBooleanPropertyEnumMap.put(Edge.NORTHWEST, NORTHWEST);
        edgeBooleanPropertyEnumMap.put(Edge.SOUTHEAST, SOUTHEAST);
        edgeBooleanPropertyEnumMap.put(Edge.SOUTHWEST, SOUTHWEST);
    });

    private static final VoxelShape NORTHEAST_AABB = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape NORTHWEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
    private static final VoxelShape SOUTHEAST_AABB = Block.box(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTHWEST_AABB = Block.box(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);

    public RodBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTHEAST, Boolean.FALSE)
                .setValue(NORTHWEST, Boolean.FALSE)
                .setValue(SOUTHEAST, Boolean.FALSE)
                .setValue(SOUTHWEST, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape voxelshape = VoxelShapes.empty();
        if (state.getValue(NORTHEAST))     { voxelshape = VoxelShapes.or(voxelshape, NORTHEAST_AABB); }
        if (state.getValue(NORTHWEST))     { voxelshape = VoxelShapes.or(voxelshape, NORTHWEST_AABB); }
        if (state.getValue(SOUTHEAST))     { voxelshape = VoxelShapes.or(voxelshape, SOUTHEAST_AABB); }
        if (state.getValue(SOUTHWEST))     { voxelshape = VoxelShapes.or(voxelshape, SOUTHWEST_AABB); }
        return voxelshape;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(pos).is(this) ? context.getLevel().getBlockState(pos) : this.defaultBlockState();
        return blockState.setValue(EDGE_TO_PROPERTY_MAP.get(Edge.getEdgeForContext(context)), true);
    }

    public boolean canBeReplaced(BlockState blockState, BlockItemUseContext context) {
        if (context.getItemInHand().getItem() == this.asItem()) {
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
    public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
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

    public boolean canContainFluid(IBlockReader p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
        return true;
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST, WATERLOGGED);
    }
}
