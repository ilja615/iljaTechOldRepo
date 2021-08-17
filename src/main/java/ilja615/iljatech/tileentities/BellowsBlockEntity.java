package ilja615.iljatech.tileentities;

import ilja615.iljatech.blocks.BellowsBlock;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.util.interactions.Wind;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BellowsBlockEntity extends BlockEntity
{
    private int compressTimer;

    public BellowsBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.BELLOWS.get(), p_155229_, p_155230_);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BellowsBlockEntity blockEntity)
    {
        if (blockEntity.compressTimer > 0)
        {
            --blockEntity.compressTimer;
            BlockState oldState = level.getBlockState(pos);
            if (blockEntity.compressTimer == 15)
            {
                level.setBlockAndUpdate(pos, oldState.setValue(BellowsBlock.COMPRESSION, 2));
                Direction facing = level.getBlockState(pos).getValue(DirectionalBlock.FACING);
                double d0 = (facing == Direction.EAST ? 0.6d : 0.0d) + (facing == Direction.WEST ? -0.6d : 0.0d);
                double d1 = (facing == Direction.UP ? 0.6d : 0.0d) + (facing == Direction.DOWN ? -0.6d : 0.0d);
                double d2 = (facing == Direction.NORTH ? -0.6d : 0.0d) + (facing == Direction.SOUTH ? 0.6d : 0.0d);
                Wind.addWind(level, pos, facing, 8, 0.4f);
            }
            if (blockEntity.compressTimer == 10)
            {
                level.setBlockAndUpdate(pos, oldState.setValue(BellowsBlock.COMPRESSION, 1));
            }
            if (blockEntity.compressTimer == 0)
            {
                level.setBlockAndUpdate(pos, oldState.setValue(BellowsBlock.COMPRESSION, 0));
            }
        }
    }

    public void compress(Level world, BlockPos pos)
    {
        BlockState oldState = world.getBlockState(pos);
        if (oldState.getBlock() == ModBlocks.BELLOWS.get() && oldState.getValue(BellowsBlock.COMPRESSION) == 0)
        {
            world.setBlockAndUpdate(pos, oldState.setValue(BellowsBlock.COMPRESSION, 1));
            this.compressTimer = 20;
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound)
    {
        super.save(compound);
        compound.putInt("CompressTimer", this.compressTimer);
        return compound;
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);

        this.compressTimer = compound.getInt("CompressTimer");
    }
}
