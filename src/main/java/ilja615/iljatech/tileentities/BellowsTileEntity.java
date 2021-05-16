package ilja615.iljatech.tileentities;

import ilja615.iljatech.blocks.BellowsBlock;
import ilja615.iljatech.entity.AbstractGasEntity;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModEntities;
import ilja615.iljatech.init.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
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
                AbstractGasEntity gasEntity = ModEntities.AIR_CLOUD.get().create(this.level);
                gasEntity.moveTo(0.5d + (double)worldPosition.getX() + d0, 0.5d + (double)worldPosition.getY() + d1, 0.5d + (double)worldPosition.getZ() + d2, 0.0f, 0.0F);
                gasEntity.lerpMotion(d0 / 2.0d, d1 / 2.0d, d2 / 2.0d);
                this.level.addFreshEntity(gasEntity);
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
