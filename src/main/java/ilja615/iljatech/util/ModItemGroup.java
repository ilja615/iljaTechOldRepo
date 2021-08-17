package ilja615.iljatech.util;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroup extends CreativeModeTab
{
    public static final ModItemGroup instance = new ModItemGroup(CreativeModeTab.TABS.length, "iljatech");

    private ModItemGroup(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(Blocks.GRAY_GLAZED_TERRACOTTA);
    }
}
