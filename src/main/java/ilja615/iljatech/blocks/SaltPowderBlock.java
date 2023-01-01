package ilja615.iljatech.blocks;

import net.minecraft.world.level.block.FallingBlock;

public class SaltPowderBlock extends FallingBlock
{
    public SaltPowderBlock(Properties p_53205_)
    {
        super(p_53205_);
    }

    @Override
    protected int getDelayAfterPlace() {
        return 10;
    }
}
