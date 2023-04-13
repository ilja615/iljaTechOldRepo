package ilja615.iljatech.blocks.stretcher;

import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.energy.IMechanicalPowerAccepter;
import ilja615.iljatech.energy.MechanicalPower;
import ilja615.iljatech.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.antlr.v4.runtime.misc.NotNull;

import javax.annotation.Nullable;

public class StretcherBlock extends BaseEntityBlock implements IMechanicalPowerAccepter
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public StretcherBlock(Properties p_49795_)
    {
        super(p_49795_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH).setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return ModBlockEntityTypes.STRETCHER.get().create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntityTypes.STRETCHER.get(), StretcherBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof StretcherBlockEntity stretcherBlockEntity)
            {
                stretcherBlockEntity.stretcherItemStackHandler.ifPresent(itemHandler -> ModUtils.DropContentsOfItemHandler(worldIn, pos, itemHandler));
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        ItemStack handStack = player.getItemInHand(handIn);
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof StretcherBlockEntity stretcherBlockEntity)
        {
            stretcherBlockEntity.stretcherItemStackHandler.ifPresent(h ->
            {
                if (player.getMainHandItem().isEmpty()) {
                    if (!h.getStackInSlot(1).isEmpty()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, h.getStackInSlot(1));
                        h.setStackInSlot(1, ItemStack.EMPTY);
                    } else if (!h.getStackInSlot(0).isEmpty()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, h.getStackInSlot(0));
                        h.setStackInSlot(0, ItemStack.EMPTY);
                    }
                } else if (player.getMainHandItem().getItem() instanceof ShovelItem) {
                    ModUtils.DropContentsOfItemHandler(worldIn, pos.relative(state.getValue(FACING)), h);
                    ModUtils.EmptySlotsOfItemHandler(h);
                } else {
                    final ItemStack[] newStack = new ItemStack[]{handStack};
                    newStack[0] = h.insertItem(0, handStack, false);
                    player.setItemInHand(handIn, newStack[0]);
                }
            });
            return InteractionResult.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        world.scheduleTick(thisPos, this, 100);
        IMechanicalPowerAccepter.super.receivePower(world, thisPos, sideFrom, amount);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand)
    {
        super.tick(state, worldIn, pos, rand);
        if (state.getBlock() != this) { return; }

        if (state.getValue(ModProperties.MECHANICAL_POWER) == MechanicalPower.ALMOST_STOPPING)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
        }
        else if (((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning())
        {
            worldIn.scheduleTick(pos, this, 10);
            worldIn.setBlockAndUpdate(pos, state.setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.ALMOST_STOPPING));
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) { p_206840_1_.add(FACING, ModProperties.MECHANICAL_POWER); }

    public BlockState rotate(BlockState p_54125_, Rotation p_54126_) {
        return p_54125_.setValue(FACING, p_54126_.rotate(p_54125_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_54122_, Mirror p_54123_) {
        return p_54122_.rotate(p_54123_.getRotation(p_54122_.getValue(FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_)
    {
        return (BlockState)this.defaultBlockState().setValue(FACING, p_196258_1_.getHorizontalDirection().getOpposite());
    }

    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState p_49232_) {
        return RenderShape.MODEL;
    }
}
