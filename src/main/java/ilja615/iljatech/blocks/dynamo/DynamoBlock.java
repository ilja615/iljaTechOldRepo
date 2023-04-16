package ilja615.iljatech.blocks.dynamo;

import ilja615.iljatech.blocks.crafter_machine.CrafterMachineBlockEntity;
import ilja615.iljatech.blocks.crusher.CrusherBlockEntity;
import ilja615.iljatech.energy.IMechanicalPowerAccepter;
import ilja615.iljatech.energy.MechanicalPower;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DynamoBlock extends BaseEntityBlock implements IMechanicalPowerAccepter
{
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public DynamoBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ModProperties.MECHANICAL_POWER, MechanicalPower.OFF));
    }

    public boolean triggerEvent(BlockState p_49226_, Level p_49227_, BlockPos p_49228_, int p_49229_, int p_49230_) {
        super.triggerEvent(p_49226_, p_49227_, p_49228_, p_49229_, p_49230_);
        BlockEntity blockentity = p_49227_.getBlockEntity(p_49228_);
        return blockentity == null ? false : blockentity.triggerEvent(p_49229_, p_49230_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.DYNAMO.get().create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntityTypes.DYNAMO.get(), DynamoBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult)
    {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof DynamoBlockEntity)
        {
            if (!world.isClientSide())
            {
                NetworkHooks.openScreen((ServerPlayer)player, (DynamoBlockEntity)tileEntity, pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void receivePower(Level world, BlockPos thisPos, Direction sideFrom, int amount)
    {
        world.scheduleTick(thisPos, this, 10);
        IMechanicalPowerAccepter.super.receivePower(world, thisPos, sideFrom, amount);
    }

    @Override
    public boolean acceptsPower(Level world, BlockPos thisPos, Direction sideFrom)
    {
        BlockState state = world.getBlockState(thisPos);
        return (state.hasProperty(FACING) && state.getValue(FACING).getAxis() == sideFrom.getAxis() && state.hasProperty(ModProperties.MECHANICAL_POWER) && !((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning());
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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(ModProperties.MECHANICAL_POWER, FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState p_49232_) {
        return RenderShape.MODEL;
    }
}
