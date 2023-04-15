package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.energy.IMechanicalPowerAccepter;
import ilja615.iljatech.energy.IMechanicalPowerSender;
import ilja615.iljatech.energy.MechanicalPower;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RodBlock extends Block
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape Y_AXIS_AABB = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape NORTHEAST_AABB = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape NORTHWEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
    private static final VoxelShape SOUTHEAST_AABB = Block.box(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTHWEST_AABB = Block.box(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
    public static final BooleanProperty CENTER = BooleanProperty.create("center");
    public static final BooleanProperty NORTHEAST = BooleanProperty.create("northeast");
    public static final BooleanProperty NORTHWEST = BooleanProperty.create("northwest");
    public static final BooleanProperty SOUTHEAST = BooleanProperty.create("southeast");
    public static final BooleanProperty SOUTHWEST = BooleanProperty.create("southwest");

    public RodBlock(BlockBehaviour.Properties p_i48404_1_) {
        super(p_i48404_1_);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTHEAST, false)
                .setValue(NORTHWEST, false)
                .setValue(SOUTHEAST, false)
                .setValue(SOUTHWEST, false)
                .setValue(CENTER, false)
                .setValue(WATERLOGGED, false));
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        VoxelShape voxelshape = Shapes.empty();
        if (state.getValue(CENTER))     { voxelshape = Shapes.or(voxelshape, Y_AXIS_AABB); }
        if (state.getValue(NORTHEAST))     { voxelshape = Shapes.or(voxelshape, NORTHEAST_AABB); }
        if (state.getValue(NORTHWEST))     { voxelshape = Shapes.or(voxelshape, NORTHWEST_AABB); }
        if (state.getValue(SOUTHEAST))     { voxelshape = Shapes.or(voxelshape, SOUTHEAST_AABB); }
        if (state.getValue(SOUTHWEST))     { voxelshape = Shapes.or(voxelshape, SOUTHWEST_AABB); }
        return voxelshape;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos()).is(this) ? context.getLevel().getBlockState(context.getClickedPos()) : this.defaultBlockState();
        BooleanProperty whichOne = getWhichOne(context);
        if (whichOne != null)
            return blockState.setValue(whichOne, true);
        return blockState;
    }

    private BooleanProperty getWhichOne(BlockPlaceContext context)
    {
        Direction clickedFace = context.getClickedFace();
        double xn = context.getClickLocation().x - context.getClickedPos().getX();
        double zn = context.getClickLocation().z - context.getClickedPos().getZ();
        if (xn >= 0 && xn <= 1 && zn >= 0 && zn <= 1)
        {
            if (clickedFace.getAxis() == Direction.Axis.Y)
            {
                if (zn < 0.5 - xn)
                    return NORTHWEST;
                if (zn > 0.5 + xn)
                    return SOUTHWEST;
                if (zn < -0.5 + xn)
                    return NORTHEAST;
                if (zn > 1.5 - xn)
                    return SOUTHEAST;
                return CENTER;
            } else {
                if (xn < 0.5 && zn < 0.5)
                    return NORTHWEST;
                if (xn < 0.5 && zn >= 0.5)
                    return SOUTHWEST;
                if (xn >= 0.5 && zn < 0.5)
                    return NORTHEAST;
                if (xn >= 0.5 && zn >= 0.5)
                    return SOUTHEAST;
            }
        }
        return null;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context)
    {
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

            BooleanProperty whichOne = getWhichOne(context);
            return whichOne == null || !state.getValue(whichOne); // It can not be "replaced" if the thing that it would place is already there.
        }
        return false;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(CENTER, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState p_204507_1_) {
        return (Boolean)p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
    }

    public boolean canContainFluid(BlockGetter p_204510_1_, BlockPos p_204510_2_, BlockState p_204510_3_, Fluid p_204510_4_) {
        return true;
    }

    public boolean isPathfindable(BlockState p_196266_1_, BlockGetter p_196266_2_, BlockPos p_196266_3_, PathComputationType p_196266_4_) {
        return false;
    }
}