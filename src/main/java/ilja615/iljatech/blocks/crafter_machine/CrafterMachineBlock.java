package ilja615.iljatech.blocks.crafter_machine;

import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.blocks.crafter_machine.CrafterMachineBlockEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class CrafterMachineBlock extends BaseEntityBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public CrafterMachineBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return ModBlockEntityTypes.CRAFTER_MACHINE.get().create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult)
    {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CrafterMachineBlockEntity)
        {
            if (!world.isClientSide())
            {
                NetworkHooks.openScreen((ServerPlayer)player, (CrafterMachineBlockEntity)tileEntity, pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof CrafterMachineBlockEntity)
            {
                Containers.dropContents(worldIn, pos, ((CrafterMachineBlockEntity)tileEntity).getItems());
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos)
    {
        int j = 0;
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof CrafterMachineBlockEntity)
        {
            for (int i = 0; i < ((CrafterMachineBlockEntity)tileEntity).chestContents.size(); i++)
            {
                if (((CrafterMachineBlockEntity)tileEntity).chestContents.get(i) != ItemStack.EMPTY) j++;
            }
        }
        return Math.min(j, 15);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (worldIn.hasNeighborSignal(pos))
        {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof CrafterMachineBlockEntity)
            {
                ((CrafterMachineBlockEntity)tileEntity).craft();
            }
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_)
    {
        return (BlockState)this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) { p_206840_1_.add(new Property[]{FACING}); }

    public BlockState rotate(BlockState p_54125_, Rotation p_54126_) {
        return p_54125_.setValue(FACING, p_54126_.rotate(p_54125_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_54122_, Mirror p_54123_) {
        return p_54122_.rotate(p_54123_.getRotation(p_54122_.getValue(FACING)));
    }

    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState p_49232_) {
        return RenderShape.MODEL;
    }
}
