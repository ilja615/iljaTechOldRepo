package ilja615.iljatech.blocks.wire;

import ilja615.iljatech.init.ModDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BarbedWireBlock extends BaseWireBlock
{
    public BarbedWireBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public void entityInside(BlockState p_51148_, Level p_51149_, BlockPos p_51150_, Entity p_51151_) {
        p_51151_.hurt(ModDamageSources.BARBED_WIRE, 1.0F);
    }
}
