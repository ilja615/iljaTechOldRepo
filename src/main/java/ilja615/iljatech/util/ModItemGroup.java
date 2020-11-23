package ilja615.iljatech.util;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup extends ItemGroup
{
    public static final ModItemGroup instance = new ModItemGroup(ItemGroup.GROUPS.length, "iljatech");

    private ModItemGroup(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(Blocks.GRAY_GLAZED_TERRACOTTA);
    }
}
