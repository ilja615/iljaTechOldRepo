package ilja615.iljatech.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.function.ToIntFunction;

public class ModUtils
{
    // Used for furnace-type blocks their light level
    public static ToIntFunction<BlockState> getLightValueLit(int lightValue) {
        return (state) -> {
            return state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
        };
    }

    // Used for any block that has item handler capability, to drop the items
    public static void DropContentsOfItemHandler(Level level, BlockPos pos, IItemHandlerModifiable handler)
    {
        final int size = handler.getSlots();
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);

        for (int i = 0; i < size; i++)
            items.set(i, handler.getStackInSlot(i));

        Containers.dropContents(level, pos, items);
    }

    public static void EmptySlotsOfItemHandler(IItemHandlerModifiable handler)
    {
        for (int i = 0; i < handler.getSlots(); i++)
            handler.setStackInSlot(i, ItemStack.EMPTY);
    }

    public static NonNullList<ItemStack> ListFromItemHandler(IItemHandlerModifiable handler)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < handler.getSlots(); i++)
            nonnulllist.set(i, handler.getStackInSlot(i));
        return nonnulllist;
    }
}
