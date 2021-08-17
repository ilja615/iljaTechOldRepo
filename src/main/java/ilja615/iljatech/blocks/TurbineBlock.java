package ilja615.iljatech.blocks;

import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.power.IMechanicalPowerSender;
import ilja615.iljatech.power.MechanicalPower;
import ilja615.iljatech.tileentities.BellowsBlockEntity;
import ilja615.iljatech.tileentities.TurbineBlockEntity;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.antlr.v4.runtime.misc.NotNull;

import javax.annotation.Nullable;

public class TurbineBlock extends BaseEntityBlock implements IMechanicalPowerSender
{
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    protected static final VoxelShape UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);
    protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 2.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 2.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 14.0D);
    protected static final VoxelShape WEST_AABB = Block.box(2.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 14.0D, 16.0D, 16.0D);

    public TurbineBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        switch ((Direction)state.getValue(FACING))
        {
            case DOWN:
            default:
                return DOWN_AABB;
            case UP:
                return UP_AABB;
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case WEST:
                return WEST_AABB;
            case EAST:
                return EAST_AABB;
        }
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(ModProperties.MECHANICAL_POWER, FACING);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity)
    {
        if (!world.isClientSide() && entity instanceof AbstractGasEntity)
        {
            if (world.getBlockState(pos).hasProperty(ModProperties.MECHANICAL_POWER))
            {
                entity.remove(Entity.RemovalReason.DISCARDED);
                BlockEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof TurbineBlockEntity)
                {
                    ((TurbineBlockEntity)tileEntity).startSpinning(world, pos, 200);
                }
            }
        }
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntityTypes.TURBINE.get(), TurbineBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return ModBlockEntityTypes.TURBINE.get().create(pos, state);
    }

    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState p_49232_) {
        return RenderShape.MODEL;
    }

}