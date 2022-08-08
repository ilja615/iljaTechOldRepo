package ilja615.iljatech.blocks.foundry;

import ilja615.iljatech.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class FoundryBlockEntity extends BlockEntity implements MenuProvider, Nameable
{
    private Component customName;
    public NonNullList<ItemStack> chestContents = NonNullList.withSize(7, ItemStack.EMPTY);
    protected int numPlayersUsing;
    private final static EmptyHandler EMPTYHANDLER = new EmptyHandler();
    public LazyOptional<IItemHandlerModifiable> cmItemStackHandler = LazyOptional.of(() -> new FoundryBlockEntity.FoundryItemStackHandler(this));
    protected LazyOptional<CraftingContainer> wrapper = LazyOptional.of(() ->
            new FoundryInventoryWrapper(cmItemStackHandler.orElse(EMPTYHANDLER)));
    private Optional<CraftingRecipe> recipeUsed = Optional.empty();

    public FoundryBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntityTypes.FOUNDRY.get(), pos, state);
    }


    public NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.chestContents = items;
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    protected Component getDefaultName()
    {
        return Component.translatable("container.foundry");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player)
    {
        return new FoundryContainer(id, playerInventory, this);
    }

    @Override
    public void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, this.chestContents);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        this.chestContents = NonNullList.withSize(this.chestContents.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.chestContents);
        cmItemStackHandler.ifPresent(h ->
        {
            for (int i = 0; i < h.getSlots(); i++)
            {
                h.setStackInSlot(i, chestContents.get(i));
            }
        });
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else return super.triggerEvent(id, type);
    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof FoundryBlock) {
            this.level.blockEvent(this.worldPosition, block, 1, this.numPlayersUsing);
            this.level.updateNeighborsAt(this.worldPosition, block);
        }
    }

    public static int getPlayersUsing(BlockGetter reader, BlockPos pos) {
        BlockState blockState = reader.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity tileEntity = reader.getBlockEntity(pos);
            if (tileEntity instanceof FoundryBlockEntity) {
                return ((FoundryBlockEntity) tileEntity).numPlayersUsing;
            }
        }
        return 0;
    }

    public static void swapContent(FoundryBlockEntity tileEntity, FoundryBlockEntity otherTileEntity) {
        NonNullList<ItemStack> list = tileEntity.getItems();
        tileEntity.setItems(otherTileEntity.getItems());
        otherTileEntity.setItems(list);
    }


    @Override
    public void invalidateCaps()
    {
        this.cmItemStackHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction direction)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return cmItemStackHandler.cast();
        }
        return super.getCapability(capability, direction);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (cmItemStackHandler != null) {
            cmItemStackHandler.invalidate();
        }
    }

    private class FoundryItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        private final FoundryBlockEntity tile;

        public FoundryItemStackHandler(FoundryBlockEntity te)
        {
            super(7);
            tile = te;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if (slot < 7) tile.chestContents.set(slot, this.stacks.get(slot));
            tile.setChanged();
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }
    }

    public void setItemStackAndSaveAndSync(int slot, ItemStack itemStack)
    {
        this.chestContents.set(slot, itemStack);
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
    }

}
