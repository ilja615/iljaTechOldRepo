package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.tileentities.CrafterMachineTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CrafterMachineBlock extends HorizontalBlock
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public CrafterMachineBlock(Properties properties)
    {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH));
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
        return ModTileEntityTypes.CRAFTER_MACHINE.get().create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof CrafterMachineTileEntity)
        {
            if (!world.isRemote())
            {
                NetworkHooks.openGui((ServerPlayerEntity)player, (CrafterMachineTileEntity)tileEntity, pos);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof CrafterMachineTileEntity)
            {
                InventoryHelper.dropItems(worldIn, pos, ((CrafterMachineTileEntity)tileEntity).getItems());
            }
        }
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState blockState) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
        int j = 0;
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof CrafterMachineTileEntity)
        {
            for (int i = 0; i < ((CrafterMachineTileEntity)tileEntity).chestContents.size(); i++)
            {
                if (((CrafterMachineTileEntity)tileEntity).chestContents.get(i) != ItemStack.EMPTY) j++;
            }
        }
        return Math.min(j, 15);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (worldIn.isBlockPowered(pos))
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof CrafterMachineTileEntity)
            {
                ((CrafterMachineTileEntity)tileEntity).craft();
            }
        }
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_)
    {
        return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) { p_206840_1_.add(new Property[]{FACING}); }
}