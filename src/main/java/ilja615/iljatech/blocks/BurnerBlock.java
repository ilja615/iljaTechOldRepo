package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.tileentities.BurnerTileEntity;
import ilja615.iljatech.tileentities.CrafterMachineTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class BurnerBlock extends HorizontalBlock
{
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final IntegerProperty ASH_LEVEL = IntegerProperty.create("ash_level", 0, 5);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public BurnerBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(ASH_LEVEL, 0));
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
        return ModTileEntityTypes.BURNER.get().create();
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof BurnerTileEntity)
            {
                InventoryHelper.dropContents(worldIn, pos, ((BurnerTileEntity)tileEntity).getItems());
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos)
    {
        if (blockState.hasProperty(ASH_LEVEL))
            return Math.min(blockState.getValue(ASH_LEVEL), 15);
        else
            return 0;
    }

    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_)
    {
        return (BlockState)this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getItemInHand(handIn);
        int burnTime = ForgeHooks.getBurnTime(stack);
        if (burnTime > 0 && !stack.hasContainerItem())
        {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof BurnerTileEntity)
            {
                final ItemStack[] newStack = new ItemStack[]{stack};
                ((BurnerTileEntity)tileEntity).burnerItemStackHandler.ifPresent(h -> newStack[0] = h.insertItem(0, stack, false));
                player.setItemInHand(handIn, newStack[0]);
                return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) { p_206840_1_.add(FACING, ASH_LEVEL, LIT); }
}
