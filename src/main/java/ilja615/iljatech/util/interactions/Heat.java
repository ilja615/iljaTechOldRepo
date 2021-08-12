package ilja615.iljatech.util.interactions;

import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.init.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Heat
{
    public static void emitHeat(World level, BlockPos startPosition)
    {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(startPosition.above());
            if (startPosition.getY() < level.getMaxBuildHeight() - 1 && state.getBlock() == Blocks.CAULDRON) {
                int value = state.getValue(CauldronBlock.LEVEL) - 1; // The value that the cauldron level would be.
                if (value >= 0) {
                    level.setBlockAndUpdate(startPosition.above(), state.setValue(CauldronBlock.LEVEL, value));
                    AbstractGasEntity gasEntity = ModEntities.STEAM_CLOUD.get().create(level);
                    gasEntity.moveTo(startPosition.getX() + 0.5f, startPosition.getY() + 1.8f, startPosition.getZ() + 0.5f, 0.0f, 0.0F);
                    gasEntity.setDeltaMovement(0.0d, 0.05d, 0.0d);
                    level.addFreshEntity(gasEntity);
                }
            }
        }
    }
}
