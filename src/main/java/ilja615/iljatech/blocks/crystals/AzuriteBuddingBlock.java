package ilja615.iljatech.blocks.crystals;

import ilja615.iljatech.init.ModBlocks;
import net.minecraft.world.level.block.Block;

public class AzuriteBuddingBlock extends AbstractBuddingCrystalBlock
{
    @Override
    Block smallBud()
    {
        return ModBlocks.AZURITE_SMALL_BUD.get();
    }

    @Override
    Block mediumBud()
    {
        return ModBlocks.AZURITE_MEDIUM_BUD.get();
    }

    @Override
    Block largeBud()
    {
        return ModBlocks.AZURITE_LARGE_BUD.get();
    }

    @Override
    Block cluster()
    {
        return ModBlocks.AZURITE_CLUSTER.get();
    }

    public AzuriteBuddingBlock(Properties p_152726_)
    {
        super(p_152726_);
    }
}
