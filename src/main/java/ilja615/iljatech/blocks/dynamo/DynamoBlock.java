package ilja615.iljatech.blocks.dynamo;

import ilja615.iljatech.blocks.crafter_machine.CrafterMachineBlockEntity;
import ilja615.iljatech.blocks.crusher.CrusherBlockEntity;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class DynamoBlock extends BaseEntityBlock
{
    public DynamoBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.CRAFTER_MACHINE.get().create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntityTypes.CRUSHER.get(), CrusherBlockEntity::tick);
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
}
