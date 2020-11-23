package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SixWayBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class PlateBlock extends Block implements IWaterLoggable
{
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP;

    protected static final VoxelShape UP_AABB = Block.makeCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape DOWN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);

    public PlateBlock(Properties p_i48440_1_)
    {
        super(p_i48440_1_);
        this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(false)).with(DOWN, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape voxelshape = VoxelShapes.empty();
        if (state.get(UP)) {
            voxelshape = VoxelShapes.or(voxelshape, UP_AABB);
        }

        if (state.get(DOWN)) {
            voxelshape = VoxelShapes.or(voxelshape, DOWN_AABB);
        }

        if (state.get(NORTH)) {
            voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);
        }

        if (state.get(EAST)) {
            voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);
        }

        if (state.get(SOUTH)) {
            voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);
        }

        if (state.get(WEST)) {
            voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);
        }
        return voxelshape;
    }

    public static BooleanProperty getPropertyFor(Direction side) {
        return FACING_TO_PROPERTY_MAP.get(side);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockPos pos = context.getPos();
        BlockState blockState = context.getWorld().getBlockState(pos);
        if (blockState.isIn(this))
        {
            // For adding to a existing block
            if (context.getFace().getAxis() == Direction.Axis.Y)
            {
                if (context.getHitVec().x - context.getPos().getX() > 0.9375D)
                    return blockState.with(EAST, true);
                if (context.getHitVec().x - context.getPos().getX() < 0.0625D)
                    return blockState.with(WEST, true);
                if (context.getHitVec().z - context.getPos().getZ() > 0.9375D)
                    return blockState.with(SOUTH, true);
                if (context.getHitVec().z - context.getPos().getZ() < 0.0625D)
                    return blockState.with(NORTH, true);
            }
            if (context.getFace().getAxis() == Direction.Axis.X)
            {
                if (context.getHitVec().y - context.getPos().getY() > 0.9375D)
                    return blockState.with(UP, true);
                if (context.getHitVec().y - context.getPos().getY() < 0.0625D)
                    return blockState.with(DOWN, true);
                if (context.getHitVec().z - context.getPos().getZ() > 0.9375D)
                    return blockState.with(SOUTH, true);
                if (context.getHitVec().z - context.getPos().getZ() < 0.0625D)
                    return blockState.with(NORTH, true);
            }
            if (context.getFace().getAxis() == Direction.Axis.Z)
            {
                if (context.getHitVec().y - context.getPos().getY() > 0.9375D)
                    return blockState.with(UP, true);
                if (context.getHitVec().y - context.getPos().getY() < 0.0625D)
                    return blockState.with(DOWN, true);
                if (context.getHitVec().x - context.getPos().getX() > 0.9375D)
                    return blockState.with(EAST, true);
                if (context.getHitVec().x - context.getPos().getX() < 0.0625D)
                    return blockState.with(WEST, true);
            }
            // Default:
            if (!blockState.get(FACING_TO_PROPERTY_MAP.get(context.getFace().getOpposite())))
                return blockState.with(FACING_TO_PROPERTY_MAP.get(context.getFace().getOpposite()), true);
            else
                return blockState;
        } else {
            // For placing a new block:
            if (context.getFace().getAxis() == Direction.Axis.Y)
            {
                if (context.getHitVec().x - context.getPos().getX() > 0.9375D)
                    return this.getDefaultState().with(EAST, true);
                if (context.getHitVec().x - context.getPos().getX() < 0.0625D)
                    return this.getDefaultState().with(WEST, true);
                if (context.getHitVec().z - context.getPos().getZ() > 0.9375D)
                    return this.getDefaultState().with(SOUTH, true);
                if (context.getHitVec().z - context.getPos().getZ() < 0.0625D)
                    return this.getDefaultState().with(NORTH, true);
                return this.getDefaultState().with(FACING_TO_PROPERTY_MAP.get(context.getFace().getOpposite()), true);
            }
            if (context.getFace().getAxis() == Direction.Axis.X)
            {
                if (context.getHitVec().y - context.getPos().getY() > 0.9375D)
                    return this.getDefaultState().with(UP, true);
                if (context.getHitVec().y - context.getPos().getY() < 0.0625D)
                    return this.getDefaultState().with(DOWN, true);
                if (context.getHitVec().z - context.getPos().getZ() > 0.9375D)
                    return this.getDefaultState().with(SOUTH, true);
                if (context.getHitVec().z - context.getPos().getZ() < 0.0625D)
                    return this.getDefaultState().with(NORTH, true);
                return this.getDefaultState().with(FACING_TO_PROPERTY_MAP.get(context.getFace().getOpposite()), true);
            }
            if (context.getFace().getAxis() == Direction.Axis.Z)
            {
                if (context.getHitVec().y - context.getPos().getY() > 0.9375D)
                    return this.getDefaultState().with(UP, true);
                if (context.getHitVec().y - context.getPos().getY() < 0.0625D)
                    return this.getDefaultState().with(DOWN, true);
                if (context.getHitVec().x - context.getPos().getX() > 0.9375D)
                    return this.getDefaultState().with(EAST, true);
                if (context.getHitVec().x - context.getPos().getX() < 0.0625D)
                    return this.getDefaultState().with(WEST, true);
                return this.getDefaultState().with(FACING_TO_PROPERTY_MAP.get(context.getFace().getOpposite()), true);
            }
            return this.getDefaultState().with(FACING_TO_PROPERTY_MAP.get(context.getFace()), true);
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
            if (context.getFace() == Direction.WEST && context.getHitVec().x - context.getPos().getX() <= 0)
                return false;
            if (context.getFace() == Direction.SOUTH && context.getHitVec().z - context.getPos().getZ() >= 1)
                return false;
            if (context.getFace() == Direction.NORTH && context.getHitVec().z - context.getPos().getZ() <= 0)
                return false;
            return true;
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

    public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
            p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
        }

        return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }

//    @OnlyIn(Dist.CLIENT)
//    public void animateTick(BlockState blockState, World worldIn, BlockPos blockPos, Random random)
//    {
//        if (blockState.getBlock() == ModBlocks.CHROME_PLATE.get().getBlock() && random.nextInt(5) == 0)
//        {
//            worldIn.addParticle(ParticleTypes.END_ROD, blockPos.getX() + random.nextFloat(), blockPos.getY() + random.nextFloat(), blockPos.getZ() + random.nextFloat(), random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D);
//        }
//    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
    }
}
