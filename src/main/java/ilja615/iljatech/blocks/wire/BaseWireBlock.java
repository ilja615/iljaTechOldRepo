package ilja615.iljatech.blocks.wire;

import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.http.impl.conn.Wire;

import java.util.Random;

public class BaseWireBlock extends Block
{
    protected static final VoxelShape FLAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final EnumProperty<WireShape> SHAPE = ModProperties.WIRE_SHAPE;
    public static final IntegerProperty DISTANCE = BlockStateProperties.STABILITY_DISTANCE;


    public BaseWireBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, WireShape.NORTH_SOUTH).setValue(DISTANCE, 7));
    }

    public VoxelShape getShape(BlockState p_49403_, BlockGetter p_49404_, BlockPos p_49405_, CollisionContext p_49406_) {
        WireShape wireShape = p_49403_.is(this) ? p_49403_.getValue(this.getShapeProperty()) : null;
        return wireShape != null && wireShape.isAscending() ? HALF_BLOCK_AABB : FLAT_AABB;
    }

    public static boolean isWire(Level p_49365_, BlockPos p_49366_) {
        return isWire(p_49365_.getBlockState(p_49366_));
    }

    public static boolean isWire(BlockState blockState) {
        return blockState.getBlock() instanceof BaseWireBlock;
    }

    @Override
    public void onPlace(BlockState p_49408_, Level level, BlockPos pos, BlockState p_49411_, boolean p_49412_) {
        this.updateState(p_49408_, level, pos, p_49412_);
    }

    protected void updateState(BlockState blockstate, Level level, BlockPos pos, Block p_55400_) {
        this.updateDir(level, pos, blockstate, false);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, 1);
        }
    }

    protected BlockState updateState(BlockState p_49390_, Level p_49391_, BlockPos p_49392_, boolean p_49393_) {
        p_49390_ = this.updateDir(p_49391_, p_49392_, p_49390_, true);
        if (!p_49391_.isClientSide()) {
            p_49391_.scheduleTick(p_49392_, this, 1);
        }
        return p_49390_;
    }

    public Property<WireShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState p_55405_, Rotation p_55406_) {
        switch(p_55406_) {
            case CLOCKWISE_180:
                switch((WireShape)p_55405_.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.NORTH_WEST);
                    case SOUTH_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.NORTH_EAST);
                    case NORTH_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.SOUTH_EAST);
                    case NORTH_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.SOUTH_WEST);
                    case NORTH_SOUTH: //Forge fix: MC-196102
                    case EAST_WEST:
                        return p_55405_;
                }
            case COUNTERCLOCKWISE_90:
                switch((WireShape)p_55405_.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.NORTH_WEST);
                    case NORTH_SOUTH:
                        return p_55405_.setValue(SHAPE, WireShape.EAST_WEST);
                    case EAST_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.NORTH_SOUTH);
                }
            case CLOCKWISE_90:
                switch((WireShape)p_55405_.getValue(SHAPE)) {
                    case ASCENDING_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return p_55405_.setValue(SHAPE, WireShape.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.NORTH_WEST);
                    case NORTH_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.NORTH_EAST);
                    case NORTH_EAST:
                        return p_55405_.setValue(SHAPE, WireShape.SOUTH_EAST);
                    case NORTH_SOUTH:
                        return p_55405_.setValue(SHAPE, WireShape.EAST_WEST);
                    case EAST_WEST:
                        return p_55405_.setValue(SHAPE, WireShape.NORTH_SOUTH);
                }
            default:
                return p_55405_;
        }
    }

    @Override
    public BlockState mirror(BlockState p_55402_, Mirror p_55403_) {
        WireShape wireshape = p_55402_.getValue(SHAPE);
        switch(p_55403_) {
            case LEFT_RIGHT:
                switch(wireshape) {
                    case ASCENDING_NORTH:
                        return p_55402_.setValue(SHAPE, WireShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return p_55402_.setValue(SHAPE, WireShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return p_55402_.setValue(SHAPE, WireShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return p_55402_.setValue(SHAPE, WireShape.NORTH_WEST);
                    case NORTH_WEST:
                        return p_55402_.setValue(SHAPE, WireShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return p_55402_.setValue(SHAPE, WireShape.SOUTH_EAST);
                    default:
                        return super.mirror(p_55402_, p_55403_);
                }
            case FRONT_BACK:
                switch(wireshape) {
                    case ASCENDING_EAST:
                        return p_55402_.setValue(SHAPE, WireShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return p_55402_.setValue(SHAPE, WireShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return p_55402_.setValue(SHAPE, WireShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return p_55402_.setValue(SHAPE, WireShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return p_55402_.setValue(SHAPE, WireShape.NORTH_EAST);
                    case NORTH_EAST:
                        return p_55402_.setValue(SHAPE, WireShape.NORTH_WEST);
                }
        }

        return super.mirror(p_55402_, p_55403_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55408_) {
        p_55408_.add(SHAPE, DISTANCE);
    }

    protected BlockState updateDir(Level level, BlockPos pos, BlockState state, boolean p_49371_) {
        WireShape wireshape = state.getValue(this.getShapeProperty());
        BlockState returnState = (new WireState(level, pos, state)).place(level.hasNeighborSignal(pos), p_49371_, wireshape).getState();
        int i = getDistance(returnState, level, pos);
        return returnState.setValue(DISTANCE, i);
    }

    public WireShape getWireDirection(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(getShapeProperty());
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos otherPos, boolean p_49382_) {
        if (!level.isClientSide && level.getBlockState(pos).is(this)) {
            this.updateState(state, level, pos, block);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState p_49387_, boolean p_49388_) {
        if (!p_49388_) {
            super.onRemove(state, level, pos, p_49387_, p_49388_);
            if (getWireDirection(state, level, pos).isAscending()) {
                level.updateNeighborsAt(pos.above(), this);
            }
            level.updateNeighborsAt(pos, this);
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState blockstate = this.defaultBlockState();
        return updateDir(context.getLevel(), context.getClickedPos(), blockstate, true);
    }

    public int getDistance(BlockState thisState, BlockGetter blockGetter, BlockPos pos) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.mutable().move(Direction.DOWN);
        BlockState blockstate = blockGetter.getBlockState(blockpos$mutableblockpos);
        int i = 7;
        if (blockstate.isFaceSturdy(blockGetter, blockpos$mutableblockpos, Direction.UP)) {
            return 0;
        }

        for(BlockPos p : WireState.getConnectionArray(pos, thisState.getValue(this.getShapeProperty()))) {
            BlockState blockstate1 = blockGetter.getBlockState(p);
            if (blockstate1.getBlock() instanceof BaseWireBlock) {
                i = Math.min(i, blockstate1.getValue(DISTANCE) + 1);
                if (i == 1) {
                    break;
                }
            }
        }

        return i;
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random rand) {
        level.updateNeighborsAt(pos, this);

        int i = getDistance(state, level, pos);
        BlockState blockstate = state.setValue(DISTANCE, Integer.valueOf(i));
        if (i == 7) {
            level.addFreshEntity(new FallingBlockEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, blockstate));
        } else if (state != blockstate) {
            level.setBlock(pos, blockstate, 3);
        }
    }
}
