package ilja615.iljatech.blocks.battery_box;

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
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class BatteryBoxContainer extends AbstractContainerMenu
{
    private final ContainerLevelAccess canInteractWithCallable;
    public BatteryBoxBlockEntity be;

    protected BatteryBoxContainer(final int windowId, final Inventory playerInventory, final BatteryBoxBlockEntity blockEntity)
    {
        super(ModContainerTypes.BATTERY_BOX.get(), windowId);
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.be = blockEntity;

        final int slotSizePlus2 = 18;

        // Main Inventory
        final int startX = 53;
        final int startY = 17;
        for (int i = 0 ; i < 4 ; ++i)
        {
            int finalColumn = i;
            be.batteryBoxItemStackHandler.ifPresent(h -> this.addSlot(new SlotItemHandler(h, finalColumn, startX + (finalColumn * slotSizePlus2), startY)));
        }

        // HotBar
        final int hotBarStartY = 136;
        final int playerInvStartX = 8;
        for (int column = 0; column < 9; ++column)
        {
            this.addSlot(new Slot(playerInventory, column, playerInvStartX + (column * slotSizePlus2), hotBarStartY));
        }

        // Main Player Inventory
        final int playerInvStartY = 78;
        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 9; column++)
            {
                this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, playerInvStartX + (column * slotSizePlus2), playerInvStartY + (row * slotSizePlus2)));
            }
        }
    }

    private static BatteryBoxBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity blockAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (blockAtPos instanceof BatteryBoxBlockEntity)
        {
            return (BatteryBoxBlockEntity) blockAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + blockAtPos);
    }

    public BatteryBoxContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data)
    {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return stillValid(canInteractWithCallable, playerIn, ModBlocks.BATTERY_BOX.get());
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 4) {
                if (!this.moveItemStackTo(itemstack1, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
