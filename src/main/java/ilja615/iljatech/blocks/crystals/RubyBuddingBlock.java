package ilja615.iljatech.blocks.crystals;

import ilja615.iljatech.init.ModBlocks;
import net.minecraft.world.level.block.Block;

public class RubyBuddingBlock extends AbstractBuddingCrystalBlock
{
    @Override
    Block smallBud()
    {
        return ModBlocks.RUBY_SMALL_BUD.get();
    }

    @Override
    Block mediumBud()
    {
        return ModBlocks.RUBY_MEDIUM_BUD.get();
    }

    @Override
    Block largeBud()
    {
        return ModBlocks.RUBY_LARGE_BUD.get();
    }

    @Override
    Block cluster()
    {
        return ModBlocks.RUBY_CLUSTER.get();
    }

    public RubyBuddingBlock(Properties p_152726_)
    {
        super(p_152726_);
    }
}
