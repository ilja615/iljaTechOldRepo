package ilja615.iljatech.blocks.crystals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;

import java.util.Random;

public abstract class AbstractBuddingCrystalBlock extends CrystalBlock
{
    private static final Direction[] DIRECTIONS = Direction.values();

    abstract Block smallBud();
    abstract Block mediumBud();
    abstract Block largeBud();
    abstract Block cluster();


    public AbstractBuddingCrystalBlock(BlockBehaviour.Properties p_152726_)
    {
        super(p_152726_);
    }

    public PushReaction getPistonPushReaction(BlockState p_152733_) {
        return PushReaction.DESTROY;
    }

    public void randomTick(BlockState p_152728_, ServerLevel p_152729_, BlockPos p_152730_, Random p_152731_) {
        if (p_152731_.nextInt(5) == 0) {
            Direction direction = DIRECTIONS[p_152731_.nextInt(DIRECTIONS.length)];
            BlockPos blockpos = p_152730_.relative(direction);
            BlockState blockstate = p_152729_.getBlockState(blockpos);
            Block block = null;
            if (canClusterGrowAtState(blockstate)) {
                block = this.smallBud();
            } else if (blockstate.is(this.smallBud()) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
                block = this.mediumBud();
            } else if (blockstate.is(this.mediumBud()) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
                block = this.largeBud();
            } else if (blockstate.is(this.largeBud()) && blockstate.getValue(CrystalClusterBlock.FACING) == direction) {
                block = this.cluster();
            }

            if (block != null) {
                BlockState blockstate1 = block.defaultBlockState().setValue(CrystalClusterBlock.FACING, direction).setValue(CrystalClusterBlock.WATERLOGGED, Boolean.valueOf(blockstate.getFluidState().getType() == Fluids.WATER));
                p_152729_.setBlockAndUpdate(blockpos, blockstate1);
            }

        }
    }

    public static boolean canClusterGrowAtState(BlockState p_152735_) {
        return p_152735_.isAir() || p_152735_.is(Blocks.WATER) && p_152735_.getFluidState().getAmount() == 8;
    }
}