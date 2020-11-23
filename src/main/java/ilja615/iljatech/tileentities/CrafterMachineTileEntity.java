package ilja615.iljatech.tileentities;

import ilja615.iljatech.blocks.CrafterMachineBlock;
import ilja615.iljatech.containers.CrafterMachineContainer;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.util.CraftingInventoryWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.Position;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class CrafterMachineTileEntity extends TileEntity implements IRecipeHolder, INamedContainerProvider, INameable
{
    private ITextComponent customName;
    public NonNullList<ItemStack> chestContents = NonNullList.withSize(9, ItemStack.EMPTY);
    protected int numPlayersUsing;
    private final static EmptyHandler EMPTYHANDLER = new EmptyHandler();
    public LazyOptional<IItemHandlerModifiable> cmItemStackHandler = LazyOptional.of(() -> new CrafterMachineItemStackHandler(this));
    protected LazyOptional<CraftingInventory> wrapper = LazyOptional.of(() ->
            new CraftingInventoryWrapper(cmItemStackHandler.orElse(EMPTYHANDLER)));
    private Optional<ICraftingRecipe> recipeUsed = Optional.empty();

    public CrafterMachineTileEntity(TileEntityType<?> typeIn) { super(typeIn); }
    public CrafterMachineTileEntity() { this(ModTileEntityTypes.CRAFTER_MACHINE.get()); }

    public NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.chestContents = items;
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }

    public ITextComponent getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    public ITextComponent getDisplayName() {
        return this.getName();
    }

    @Nullable
    public ITextComponent getCustomName() {
        return this.customName;
    }

    protected ITextComponent getDefaultName()
    {
        return new TranslationTextComponent("container.crafting_machine");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new CrafterMachineContainer(id, playerInventory, this);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, this.chestContents);
        return compound;
    }

    @Override
    public void read(BlockState blockState, CompoundNBT compound)
    {
        super.read(blockState, compound);
        this.chestContents = NonNullList.withSize(this.chestContents.size(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.chestContents);
        cmItemStackHandler.ifPresent(h ->
        {
            for (int i = 0; i < h.getSlots(); i++)
            {
                h.setStackInSlot(i, chestContents.get(i));
            }
        });
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else return super.receiveClientEvent(id, type);
    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof CrafterMachineBlock) {
            this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, block);
        }
    }

    public static int getPlayersUsing(IBlockReader reader, BlockPos pos) {
        BlockState blockState = reader.getBlockState(pos);
        if (blockState.hasTileEntity()) {
            TileEntity tileEntity = reader.getTileEntity(pos);
            if (tileEntity instanceof CrafterMachineTileEntity) {
                return ((CrafterMachineTileEntity) tileEntity).numPlayersUsing;
            }
        }
        return 0;
    }

    public static void swapContent(CrafterMachineTileEntity tileEntity, CrafterMachineTileEntity otherTileEntity) {
        NonNullList<ItemStack> list = tileEntity.getItems();
        tileEntity.setItems(otherTileEntity.getItems());
        otherTileEntity.setItems(list);
    }

    @Override
    protected void invalidateCaps()
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
    public void remove() {
        super.remove();
        if (cmItemStackHandler != null) {
            cmItemStackHandler.invalidate();
        }
    }

    private class CrafterMachineItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        private final CrafterMachineTileEntity tile;

        public CrafterMachineItemStackHandler(CrafterMachineTileEntity te)
        {
            super(9);
            tile = te;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if (slot < 9) tile.chestContents.set(slot, this.stacks.get(slot));
            tile.markDirty();
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
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 2);
    }

    @Override
    public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
        recipeUsed = Optional.ofNullable((ICraftingRecipe) recipe);
    }

    @Nullable
    @Override
    public IRecipe<?> getRecipeUsed() {
        return recipeUsed.orElse(null);
    }

    public void craft() {
        if (this.hasWorld()) {
            cmItemStackHandler.ifPresent(h ->
            {
                for (int i = 0; i < h.getSlots(); i++)
                {
                    h.setStackInSlot(i, chestContents.get(i));
                }
            });
            wrapper.ifPresent(w ->
            {
                recipeUsed = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, w, world).filter(r -> canUseRecipe(world, null, r));
                ItemStack outPutStack = recipeUsed.map(r -> r.getCraftingResult(w)).orElse(ItemStack.EMPTY);
                BlockPos pos = this.pos;
                if (this.getBlockState().hasProperty(CrafterMachineBlock.FACING))
                {
                    Direction side = this.getBlockState().get(CrafterMachineBlock.FACING);
                    double x = pos.getX() + 0.5D + 0.7D * (double) side.getXOffset();
                    double y = pos.getY() + 0.5D + 0.7D * (double) side.getYOffset();
                    double z = pos.getZ() + 0.5D + 0.7D * (double) side.getZOffset();

                    // Lower the dispense position slightly when shooting from the side
                    if (side.getAxis().isHorizontal()) {
                        y -= 0.2D;
                    }

                    DefaultDispenseItemBehavior.doDispense(world, outPutStack, 6, side, new Position(x, y, z));

                    world.playEvent(1000, pos, 0); // Play dispense sound
                    world.playEvent(2000, pos, side.getIndex()); // Spawn dispense particles
                }
                if (outPutStack != ItemStack.EMPTY) {
                    for (int i = 0; i < chestContents.size(); i++)
                    {
                        if (chestContents.get(i).hasContainerItem())
                            chestContents.set(i, chestContents.get(i).getContainerItem());
                        else chestContents.set(i, ItemStack.EMPTY);
                    }
                }
            });
        }
    }
}