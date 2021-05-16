package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModItems;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.power.IMechanicalPowerAccepter;
import ilja615.iljatech.power.IMechanicalPowerSender;
import ilja615.iljatech.power.MechanicalPower;
import ilja615.iljatech.tileentities.BellowsTileEntity;
import ilja615.iljatech.tileentities.BurnerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
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

public class BellowsBlock extends Block
{
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final IntegerProperty COMPRESSION = IntegerProperty.create("compression", 0, 2);

    public BellowsBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(COMPRESSION, 0));
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
        return ModTileEntityTypes.BELLOWS.get().create();
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, COMPRESSION);
    }

    @Override
    public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player)
    {
        if (player.swingingArm != null && player.getItemInHand(player.swingingArm).getItem() == ModItems.IRON_HAMMER.get())
        {
            activate(worldIn, pos);
        }
        super.attack(state, worldIn, pos, player);
    }

    @Override
    public void fallOn(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        if (worldIn.getBlockState(pos).hasProperty(FACING) && worldIn.getBlockState(pos).getValue(FACING).getAxis() != Direction.Axis.Y)
        {
            activate(worldIn, pos);
        }

        super.fallOn(worldIn, pos, entityIn, fallDistance);
    }

    private static void activate(World world, BlockPos pos)
    {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof BellowsTileEntity)
        {
            ((BellowsTileEntity)tileEntity).compress(world, pos);
        }
    }
}
