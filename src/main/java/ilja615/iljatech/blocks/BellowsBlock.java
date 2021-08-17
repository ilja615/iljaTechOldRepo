package ilja615.iljatech.blocks;

import ilja615.iljatech.init.ModItems;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.tileentities.BellowsBlockEntity;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.antlr.v4.runtime.misc.NotNull;

import javax.annotation.Nullable;

public class BellowsBlock extends BaseEntityBlock
{
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final IntegerProperty COMPRESSION = IntegerProperty.create("compression", 0, 2);

    public BellowsBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(COMPRESSION, 0));
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntityTypes.BELLOWS.get(), BellowsBlockEntity::tick);
    }

    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return ModBlockEntityTypes.BELLOWS.get().create(pos, state);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, COMPRESSION);
    }

    @Override
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player player)
    {
        if (player.swingingArm != null && player.getItemInHand(player.swingingArm).getItem() == ModItems.IRON_HAMMER.get())
        {
            activate(worldIn, pos);
        }
        super.attack(state, worldIn, pos, player);
    }

    @Override
    public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance)
    {
        if (worldIn.getBlockState(pos).hasProperty(FACING) && worldIn.getBlockState(pos).getValue(FACING).getAxis() != Direction.Axis.Y)
        {
            activate(worldIn, pos);
        }

        super.fallOn(worldIn, state, pos, entityIn, fallDistance);
    }

    private static void activate(Level world, BlockPos pos)
    {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof BellowsBlockEntity)
        {
            ((BellowsBlockEntity)tileEntity).compress(world, pos);
        }
    }
}
