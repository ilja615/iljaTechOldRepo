package ilja615.iljatech.blocks.foundry;

import ilja615.iljatech.util.containers.MaxStackSize1Slot;
import ilja615.iljatech.util.containers.ResultSlot;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class FoundryContainer extends AbstractContainerMenu
{
    private final ContainerLevelAccess canInteractWithCallable;
    public FoundryBlockEntity te;

    public FoundryContainer(final int windowId, final Inventory playerInventory, final FoundryBlockEntity tileEntity)
    {
        super(ModContainerTypes.FOUNDRY.get(), windowId);
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());
        this.te = tileEntity;

        // Main Inventory
        final int startX = 17;
        final int startY = 17;
        final int slotSizePlus2 = 18;
        for (int i = 0 ; i < 4 ; ++i)
        {
            int finalColumn = i;
            tileEntity.foundryItemStackHandler.ifPresent(h -> this.addSlot(new SlotItemHandler(h, finalColumn, startX + (finalColumn * slotSizePlus2), startY)));
        }
        tileEntity.foundryItemStackHandler.ifPresent(h -> this.addSlot(new SlotItemHandler(h, 4, 35, 53))); // Fuel
        tileEntity.foundryItemStackHandler.ifPresent(h -> this.addSlot(new ResultSlot(h, 5, 143, 35))); // Output

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

    public ItemStack quickMoveStack(Player p_38986_, int p_38987_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_38987_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_38987_ == 5) {
                if (!this.moveItemStackTo(itemstack1, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (p_38987_ > 4) {
                if (this.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 4, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
                        return ItemStack.EMPTY;
                }
                else if (p_38987_ >= 6 && p_38987_ < 29) {
                    if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (p_38987_ >= 29 && p_38987_ < 38 && !this.moveItemStackTo(itemstack1, 6, this.slots.size() - 9, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 6, this.slots.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(p_38986_, itemstack1);
        }

        return itemstack;
    }

    protected boolean isFuel(ItemStack itemStack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(itemStack, RecipeType.SMELTING) > 0;
    }
}
