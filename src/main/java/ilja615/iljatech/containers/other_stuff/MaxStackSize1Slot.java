package ilja615.iljatech.containers.other_stuff;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class MaxStackSize1Slot extends SlotItemHandler
{
    public MaxStackSize1Slot(IItemHandlerModifiable itemHandler, int index, int x, int y)
    {
        super(itemHandler, index, x, y);
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
}