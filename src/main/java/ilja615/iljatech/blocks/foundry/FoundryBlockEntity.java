package ilja615.iljatech.blocks.foundry;

import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoundryBlockEntity extends BlockEntity implements MenuProvider, Nameable
{
    private Component customName;
    public NonNullList<ItemStack> chestContents = NonNullList.withSize(7, ItemStack.EMPTY);
    protected int numPlayersUsing;
    private final static EmptyHandler EMPTYHANDLER = new EmptyHandler();
    public LazyOptional<IItemHandlerModifiable> foundryItemStackHandler = LazyOptional.of(() -> new FoundryBlockEntity.FoundryItemStackHandler(this));

    private int processingTime;

    public FoundryBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntityTypes.FOUNDRY.get(), pos, state);
    }

    @Nullable
    public List<FoundryRecipe> getRecipes()
    {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FOUNDRY.get());
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
        compound.putInt("ProcessingTime", this.processingTime);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        this.chestContents = NonNullList.withSize(this.chestContents.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.chestContents);
        foundryItemStackHandler.ifPresent(h ->
        {
            for (int i = 0; i < h.getSlots(); i++)
            {
                h.setStackInSlot(i, chestContents.get(i));
            }
        });
        this.processingTime = compound.getInt("ProcessingTime");
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
        this.foundryItemStackHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction direction)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return foundryItemStackHandler.cast();
        }
        return super.getCapability(capability, direction);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (foundryItemStackHandler != null) {
            foundryItemStackHandler.invalidate();
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

    public static void tick(Level level, BlockPos blockPos, BlockState state, FoundryBlockEntity blockEntity)
    {
        Direction d = state.getValue(FoundryBlock.FACING);
        BlockPos midBlock = blockPos.relative(d.getOpposite());
        if (PatchouliAPI.get().getMultiblock(new ResourceLocation("iljatech:foundry_multiblock")).validate(level, blockPos) != null)
        {
            blockEntity.foundryItemStackHandler.ifPresent(itemHandler ->
            {
                boolean foundRecipe = false;
                List<FoundryRecipe> recipes = blockEntity.getRecipes();
                for (FoundryRecipe r : recipes)
                {
                    ItemStack resultingStack = r.result.copy();

                    HashSet<Item> listOfRecipe = new HashSet();
                    for (Ingredient i : r.ingredients)
                    {
                        listOfRecipe.add(i.getItems()[0].getItem());
                    }
                    HashSet<Item> listOfInventory = new HashSet();
                    listOfInventory.add(blockEntity.chestContents.get(0).getItem()); listOfInventory.add(blockEntity.chestContents.get(1).getItem()); listOfInventory.add(blockEntity.chestContents.get(2).getItem()); listOfInventory.add(blockEntity.chestContents.get(3).getItem());

                    if (listOfRecipe.equals(listOfInventory))
                    {
                        // A matching recipe was found. Now then:
                        foundRecipe = true;

                        blockEntity.processingTime++;
                        if (level.isClientSide)
                            level.addParticle(ParticleTypes.SMOKE, midBlock.getX() + 0.5 + level.random.nextFloat()*0.6f - 0.3f, midBlock.getY() + 4 + level.random.nextFloat()*0.6f - 0.3f, midBlock.getZ() + 0.5 + level.random.nextFloat()*0.6f - 0.3f, 0.0d, 0.0d, 0.0d);

                        if (blockEntity.processingTime >= 60)
                        {
                            if (itemHandler.getStackInSlot(6).isEmpty()) // 6 is output slot
                            {
                                // In this case a new result itemstack is added with 1 of the result.
                                itemHandler.getStackInSlot(0).shrink(1);
                                itemHandler.getStackInSlot(1).shrink(1);
                                itemHandler.getStackInSlot(2).shrink(1);
                                itemHandler.getStackInSlot(3).shrink(1);
                                itemHandler.setStackInSlot(6, resultingStack);
                            } else if (itemHandler.getStackInSlot(6).getItem() == resultingStack.getItem() && itemHandler.getStackInSlot(6).getCount() + resultingStack.getCount() <= itemHandler.getStackInSlot(6).getMaxStackSize()) {
                                // In this case the result itemstack is added to what was already there
                                itemHandler.getStackInSlot(0).shrink(1);
                                itemHandler.getStackInSlot(1).shrink(1);
                                itemHandler.getStackInSlot(2).shrink(1);
                                itemHandler.getStackInSlot(3).shrink(1);
                                itemHandler.getStackInSlot(6).grow(resultingStack.getCount());
                            }
                            blockEntity.processingTime = 0;
                            blockEntity.setChanged();
                        }
                        return;
                    }
                }
                if (!foundRecipe)
                {
                    // In this case no recipe match was found. (Because otherwise it would have returned.)
                    blockEntity.processingTime = 0; // Resset the processingtime, in case the recipe got for example interrupted halfway through or so.
                }
            });
        }
    }
}
