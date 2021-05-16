package ilja615.iljatech.entity;

import ilja615.iljatech.blocks.StokedFireBlock;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;

public class AirEntity extends AbstractGasEntity
{
    public AirEntity(EntityType<? extends AbstractGasEntity> entityTypeIn, World worldIn)
    {
        super(entityTypeIn, worldIn);
    }

    @Override
    BasicParticleType getParticle()
    {
        return ParticleTypes.POOF;
    }

    @Override
    int maxLifeTime()
    {
        return 120;
    }

    @Override
    protected void onInsideBlock(BlockState state)
    {
        super.onInsideBlock(state);
        if (this.level.isClientSide)
        {
            return;
        }
        if (state.getBlock() == Blocks.FIRE)
        {
            if (level.getBlockState(this.blockPosition()).getBlock() == Blocks.FIRE)
                level.setBlock(this.blockPosition(), ModBlocks.STOKED_FIRE.get().defaultBlockState(), 3);
        }
        else if (state.getBlock() == ModBlocks.STOKED_FIRE.get())
        {
            if (level.getBlockState(this.blockPosition()).getBlock() == ModBlocks.STOKED_FIRE.get())
                level.setBlock(this.blockPosition(), ModBlocks.STOKED_FIRE.get().defaultBlockState().setValue(StokedFireBlock.AIR, 3), 3);
        }
    }
}
