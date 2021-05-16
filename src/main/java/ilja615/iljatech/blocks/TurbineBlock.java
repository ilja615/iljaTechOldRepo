package ilja615.iljatech.blocks;

import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.power.IMechanicalPowerAccepter;
import ilja615.iljatech.power.IMechanicalPowerSender;
import ilja615.iljatech.power.MechanicalPower;
import ilja615.iljatech.tileentities.BellowsTileEntity;
import ilja615.iljatech.tileentities.TurbineTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

public class TurbineBlock extends Block implements IMechanicalPowerSender
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

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
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

    public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos) {
        return VoxelShapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.block();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return ModTileEntityTypes.TURBINE.get().create();
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(ModProperties.MECHANICAL_POWER, FACING);
    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity)
    {
        if (!world.isClientSide() && entity instanceof AbstractGasEntity)
        {
            if (world.getBlockState(pos).hasProperty(ModProperties.MECHANICAL_POWER))
            {
                entity.remove();
                TileEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof TurbineTileEntity)
                {
                    ((TurbineTileEntity)tileEntity).startSpinning(world, pos, 200);
                }
            }
        }
    }
}