package ilja615.iljatech.blocks.dynamo;

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

public class DynamoContainer extends AbstractContainerMenu
{
    private final ContainerLevelAccess canInteractWithCallable;
    public DynamoBlockEntity be;

    protected DynamoContainer(final int windowId, final Inventory playerInventory, final DynamoBlockEntity blockEntity)
    {
        super(ModContainerTypes.DYNAMO.get(), windowId);
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.be = blockEntity;

        final int slotSizePlus2 = 18;

        // HotBar
        final int hotBarStartY = 104;
        final int playerInvStartX = 8;
        for (int column = 0; column < 9; ++column)
        {
            this.addSlot(new Slot(playerInventory, column, playerInvStartX + (column * slotSizePlus2), hotBarStartY));
        }

        // Main Player Inventory
        final int playerInvStartY = 46;
        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 9; column++)
            {
                this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, playerInvStartX + (column * slotSizePlus2), playerInvStartY + (row * slotSizePlus2)));
            }
        }
    }

    private static DynamoBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity blockAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (blockAtPos instanceof DynamoBlockEntity)
        {
            return (DynamoBlockEntity) blockAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + blockAtPos);
    }

    public DynamoContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data)
    {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return stillValid(canInteractWithCallable, playerIn, ModBlocks.CRAFTER_MACHINE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_)
    {
        return null;
    }
}
