package ilja615.iljatech.blocks.crafter_machine;

import ilja615.iljatech.init.ModBlockEntityTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.PositionImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
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

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Nameable;

public class CrafterMachineBlockEntity extends BlockEntity implements RecipeHolder, MenuProvider, Nameable
{
    private Component customName;
    public NonNullList<ItemStack> chestContents = NonNullList.withSize(9, ItemStack.EMPTY);
    protected int numPlayersUsing;
    private final static EmptyHandler EMPTYHANDLER = new EmptyHandler();
    public LazyOptional<IItemHandlerModifiable> cmItemStackHandler = LazyOptional.of(() -> new CrafterMachineItemStackHandler(this));
    protected LazyOptional<CraftingContainer> wrapper = LazyOptional.of(() ->
            new CraftingInventoryWrapper(cmItemStackHandler.orElse(EMPTYHANDLER)));
    private Optional<CraftingRecipe> recipeUsed = Optional.empty();

    public CrafterMachineBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.CRAFTER_MACHINE.get(), p_155229_, p_155230_);
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
        return Component.translatable("container.crafting_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player)
    {
        return new CrafterMachineContainer(id, playerInventory, this);
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
        if (block instanceof CrafterMachineBlock) {
            this.level.blockEvent(this.worldPosition, block, 1, this.numPlayersUsing);
            this.level.updateNeighborsAt(this.worldPosition, block);
        }
    }

    public static int getPlayersUsing(BlockGetter reader, BlockPos pos) {
        BlockState blockState = reader.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity tileEntity = reader.getBlockEntity(pos);
            if (tileEntity instanceof CrafterMachineBlockEntity) {
                return ((CrafterMachineBlockEntity) tileEntity).numPlayersUsing;
            }
        }
        return 0;
    }

    public static void swapContent(CrafterMachineBlockEntity tileEntity, CrafterMachineBlockEntity otherTileEntity) {
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

    private class CrafterMachineItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        private final CrafterMachineBlockEntity tile;

        public CrafterMachineItemStackHandler(CrafterMachineBlockEntity te)
        {
            super(9);
            tile = te;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if (slot < 9) tile.chestContents.set(slot, this.stacks.get(slot));
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

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        recipeUsed = Optional.ofNullable((CraftingRecipe) recipe);
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return recipeUsed.orElse(null);
    }

    public void craft() {
        if (this.hasLevel()) {
            cmItemStackHandler.ifPresent(h ->
            {
                for (int i = 0; i < h.getSlots(); i++)
                {
                    h.setStackInSlot(i, chestContents.get(i));
                }
            });
            wrapper.ifPresent(w ->
            {
                recipeUsed = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, w, level).filter(r -> setRecipeUsed(level, null, r));
                ItemStack outPutStack = recipeUsed.map(r -> r.assemble(w)).orElse(ItemStack.EMPTY);
                BlockPos pos = this.worldPosition;
                if (this.getBlockState().hasProperty(CrafterMachineBlock.FACING))
                {
                    Direction side = this.getBlockState().getValue(CrafterMachineBlock.FACING);
                    double x = pos.getX() + 0.5D + 0.7D * (double) side.getStepX();
                    double y = pos.getY() + 0.5D + 0.7D * (double) side.getStepY();
                    double z = pos.getZ() + 0.5D + 0.7D * (double) side.getStepZ();

                    // Lower the dispense position slightly when shooting from the side
                    if (side.getAxis().isHorizontal()) {
                        y -= 0.2D;
                    }

                    DefaultDispenseItemBehavior.spawnItem(level, outPutStack, 6, side, new PositionImpl(x, y, z));

                    level.levelEvent(1000, pos, 0); // Play dispense sound
                    level.levelEvent(2000, pos, side.get3DDataValue()); // Spawn dispense particles
                }
                if (outPutStack != ItemStack.EMPTY) {
                    for (int i = 0; i < chestContents.size(); i++)
                    {
                        if (chestContents.get(i).hasCraftingRemainingItem())
                            chestContents.set(i, chestContents.get(i).getCraftingRemainingItem());
                        else chestContents.set(i, ItemStack.EMPTY);
                    }
                }
            });
        }
    }
}