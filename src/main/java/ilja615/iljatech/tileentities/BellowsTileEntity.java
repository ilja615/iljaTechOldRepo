package ilja615.iljatech.tileentities;

import ilja615.iljatech.blocks.BellowsBlock;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.util.interactions.Wind;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BellowsTileEntity extends TileEntity implements ITickableTileEntity
{
    public BellowsTileEntity(TileEntityType<?> tileEntityTypeIn) { super(tileEntityTypeIn); }
    public BellowsTileEntity() { this(ModTileEntityTypes.BELLOWS.get()); }
    private int compressTimer;

    @Override
    public void tick()
    {
        if (this.compressTimer > 0)
        {
            --this.compressTimer;
            BlockState oldState = this.level.getBlockState(worldPosition);
            if (this.compressTimer == 15)
            {
                this.level.setBlockAndUpdate(worldPosition, oldState.setValue(BellowsBlock.COMPRESSION, 2));
                Direction facing = level.getBlockState(worldPosition).getValue(DirectionalBlock.FACING);
                double d0 = (facing == Direction.EAST ? 0.6d : 0.0d) + (facing == Direction.WEST ? -0.6d : 0.0d);
                double d1 = (facing == Direction.UP ? 0.6d : 0.0d) + (facing == Direction.DOWN ? -0.6d : 0.0d);
                double d2 = (facing == Direction.NORTH ? -0.6d : 0.0d) + (facing == Direction.SOUTH ? 0.6d : 0.0d);
                Wind.addWind(level, this.getBlockPos(), facing, 8, 0.4f);
            }
            if (this.compressTimer == 10)
            {
                this.level.setBlockAndUpdate(worldPosition, oldState.setValue(BellowsBlock.COMPRESSION, 1));
            }
            if (this.compressTimer == 0)
            {
                this.level.setBlockAndUpdate(worldPosition, oldState.setValue(BellowsBlock.COMPRESSION, 0));
            }
        }
    }

    public void compress(World world, BlockPos pos)
    {
        BlockState oldState = world.getBlockState(pos);
        if (oldState.getBlock() == ModBlocks.BELLOWS.get() && oldState.getValue(BellowsBlock.COMPRESSION) == 0)
        {
            world.setBlockAndUpdate(pos, oldState.setValue(BellowsBlock.COMPRESSION, 1));
            this.compressTimer = 20;
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {
        super.save(compound);
        compound.putInt("CompressTimer", this.compressTimer);
        return compound;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound)
    {
        super.load(blockState, compound);

        this.compressTimer = compound.getInt("CompressTimer");
    }
}
