package ilja615.iljatech.blocks.crystals;

import ilja615.iljatech.init.ModBlocks;
import net.minecraft.world.level.block.Block;

public class CassiteriteBuddingBlock extends AbstractBuddingCrystalBlock
{
    @Override
    Block smallBud()
    {
        return ModBlocks.CASSITERITE_SMALL_BUD.get();
    }

    @Override
    Block mediumBud()
    {
        return ModBlocks.CASSITERITE_MEDIUM_BUD.get();
    }

    @Override
    Block largeBud()
    {
        return ModBlocks.CASSITERITE_LARGE_BUD.get();
    }

    @Override
    Block cluster()
    {
        return ModBlocks.CASSITERITE_CLUSTER.get();
    }

    public CassiteriteBuddingBlock(Properties p_152726_)
    {
        super(p_152726_);
    }
}
