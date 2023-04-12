package ilja615.iljatech.blocks.foundry;

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

public class ChuteContainer extends AbstractContainerMenu
{
    private final ContainerLevelAccess canInteractWithCallable;
    public ChuteBlockEntity te;

    public ChuteContainer(final int windowId, final Inventory playerInventory, final ChuteBlockEntity tileEntity)
    {
        super(ModContainerTypes.CHUTE.get(), windowId);
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());
        this.te = tileEntity;

        // Main Inventory
        final int slotSizePlus2 = 18;
        tileEntity.chuteItemStackHandler.ifPresent(h -> this.addSlot(new SlotItemHandler(h, 0, 70, 25)));
        tileEntity.chuteItemStackHandler.ifPresent(h -> this.addSlot(new SlotItemHandler(h, 1, 88, 25)));
        tileEntity.chuteItemStackHandler.ifPresent(h -> this.addSlot(new SlotItemHandler(h, 2, 70, 43)));
        tileEntity.chuteItemStackHandler.ifPresent(h -> this.addSlot(new SlotItemHandler(h, 3, 88, 43)));

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

    private static ChuteBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof ChuteBlockEntity)
        {
            return (ChuteBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    public ChuteContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data)
    {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
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

    @Override
    public boolean stillValid(Player playerIn)
    {
        return stillValid(canInteractWithCallable, playerIn, ModBlocks.BRICK_CHUTE.get());
    }
}