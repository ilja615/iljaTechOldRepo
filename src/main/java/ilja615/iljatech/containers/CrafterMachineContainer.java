package ilja615.iljatech.containers;

import ilja615.iljatech.containers.other_stuff.MaxStackSize1Slot;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModContainerTypes;
import ilja615.iljatech.tileentities.CrafterMachineTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Objects;

public class CrafterMachineContainer extends Container
{
    private final IWorldPosCallable canInteractWithCallable;
    private CrafterMachineTileEntity te;

    public CrafterMachineContainer(final int windowId, final PlayerInventory playerInventory, final CrafterMachineTileEntity tileEntity)
    {
        super(ModContainerTypes.CRAFTER_MACHINE.get(), windowId);
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());
        this.te = tileEntity;

        // Main Inventory
        final int startX = 62;
        final int startY = 17;
        final int slotSizePlus2 = 18;
        for (int row = 0 ; row < 3 ; ++row)
        {
            for (int column = 0 ; column < 3 ; ++column)
            {
                int finalRow = row;
                int finalColumn = column;
                tileEntity.cmItemStackHandler.ifPresent(h -> this.addSlot(new MaxStackSize1Slot(h, finalRow * 3 + finalColumn, startX + (finalColumn * slotSizePlus2), startY + (finalRow * slotSizePlus2))));
            }
        }

        // HotBar
        final int hotBarStartY = 142;
        final int playerInvStartX = 8;
        for (int column = 0; column < 9; ++column)
        {
            this.addSlot(new Slot(playerInventory, column, playerInvStartX + (column * slotSizePlus2), hotBarStartY));
        }

        // Main Player Inventory
        final int playerInvStartY = 84;
        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 9; column++)
            {
                this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, playerInvStartX + (column * slotSizePlus2), playerInvStartY + (row * slotSizePlus2)));
            }
        }
    }

    private static CrafterMachineTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof CrafterMachineTileEntity)
        {
            return (CrafterMachineTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    public CrafterMachineContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data)
    {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return isWithinUsableDistance(canInteractWithCallable, playerIn, ModBlocks.CRAFTER_MACHINE.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
    {
        //this.inventorySlots.forEach(s -> { if (s.getHasStack()) System.out.println("Slot "+index+" : "+s.getStack()); else System.out.println("Slot "+index+" : "+"Empty Item Stack");});
        ItemStack itemStack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            final ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();
            if (index < 9)
            {
                if (!this.mergeItemStack(itemStack1, 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemStack1, 0, 9, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }
        return itemStack;
    }
}
