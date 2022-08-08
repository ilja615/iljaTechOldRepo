package ilja615.iljatech.blocks.foundry;

import ilja615.iljatech.containers.MaxStackSize1Slot;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class FoundryContainer extends AbstractContainerMenu
{
    private final ContainerLevelAccess canInteractWithCallable;
    private FoundryBlockEntity te;

    public FoundryContainer(final int windowId, final Inventory playerInventory, final FoundryBlockEntity tileEntity)
    {
        super(ModContainerTypes.FOUNDRY.get(), windowId);
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());
        this.te = tileEntity;

        // Main Inventory
        final int startX = 8;
        final int startY = 17;
        final int slotSizePlus2 = 18;
        for (int i = 0 ; i < 4 ; ++i)
        {
            int finalColumn = i;
            tileEntity.cmItemStackHandler.ifPresent(h -> this.addSlot(new MaxStackSize1Slot(h, finalColumn, startX + (finalColumn * slotSizePlus2), startY)));
        }
        tileEntity.cmItemStackHandler.ifPresent(h -> this.addSlot(new MaxStackSize1Slot(h, 4, 35, 53)));
        tileEntity.cmItemStackHandler.ifPresent(h -> this.addSlot(new MaxStackSize1Slot(h, 5, 83, 35)));
        tileEntity.cmItemStackHandler.ifPresent(h -> this.addSlot(new MaxStackSize1Slot(h, 6, 143, 35)));

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

    private static FoundryBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof FoundryBlockEntity)
        {
            return (FoundryBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    public FoundryContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data)
    {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return stillValid(canInteractWithCallable, playerIn, ModBlocks.BRICK_FOUNDRY.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index)
    {
        //this.inventorySlots.forEach(s -> { if (s.getHasStack()) System.out.println("Slot "+index+" : "+s.getStack()); else System.out.println("Slot "+index+" : "+"Empty Item Stack");});
        ItemStack itemStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem())
        {
            final ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (index < 9)
            {
                if (!this.moveItemStackTo(itemStack1, 9, this.slots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemStack1, 0, 9, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }
            else
            {
                slot.setChanged();
            }
        }
        return itemStack;
    }
}
