package ilja615.iljatech.blocks.foundry;

import ilja615.iljatech.blocks.crafter_machine.CraftingInventoryWrapper;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModRecipeTypes;
import ilja615.iljatech.util.CountedIngredient;
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
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
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
    public LazyOptional<IItemHandlerModifiable> foundryItemStackHandler = LazyOptional.of(() -> new FoundryBlockEntity.FoundryItemStackHandler(this));

    private int processingTime;
    private int fuelTime;
    private int maxTimeOfFuel;

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
        compound.putInt("FuelTime", this.fuelTime);
        compound.putInt("MaxTimeOfFuel", this.maxTimeOfFuel);
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
        this.fuelTime = compound.getInt("FuelTime");
        this.maxTimeOfFuel = compound.getInt("MaxTimeOfFuel");
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
    }

    public void setItemStackAndSaveAndSync(int slot, ItemStack itemStack)
    {
        this.chestContents.set(slot, itemStack);
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
    }

    public boolean isBurning()
    {
        return this.fuelTime > 0;
    }

    public int getLitProgress() {
        int i = this.maxTimeOfFuel;
        if (i == 0) {
            i = 200;
        }

        return this.fuelTime * 13 / i;
    }

    public int getProgress()
    {
        return this.processingTime * 24 / 60;
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

                // Burns the current fuel
                if (blockEntity.isBurning())
                {
                    blockEntity.fuelTime--;
                    if (level.isClientSide)
                        level.addParticle(ParticleTypes.SMOKE, midBlock.getX() + 0.5 + level.random.nextFloat()*0.6f - 0.3f, midBlock.getY() + 4 + level.random.nextFloat()*0.6f - 0.3f, midBlock.getZ() + 0.5 + level.random.nextFloat()*0.6f - 0.3f, 0.0d, 0.0d, 0.0d);
                }

                ArrayList<Item> listOfInventory = new ArrayList();
                for (int i = 0; i <= 3; i++)
                {
                    if (!itemHandler.getStackInSlot(i).isEmpty()) listOfInventory.add(itemHandler.getStackInSlot(i).getItem());
                }
                List<FoundryRecipe> recipes = blockEntity.getRecipes();
                for (FoundryRecipe r : recipes)
                {
                    ItemStack resultingStack = r.result.copy();

                    ArrayList<Item> listOfRecipe = new ArrayList();
                    for (Ingredient i : r.ingredients)
                    {
                        if (!i.getItems()[0].isEmpty()) listOfRecipe.add(i.getItems()[0].getItem());
                    }

                    if (listOfRecipe.containsAll(listOfInventory) && listOfInventory.containsAll(listOfRecipe))
                    {
                        foundRecipe = true;
                        int[] amountsToBeSubstracted = {1, 1, 1, 1};

                        // Technically a matching recipe was found but amounts have to be still checked
                        for (Ingredient i : r.ingredients)
                        {
                            if (i instanceof CountedIngredient countedIngredient)
                            {
                                // Find all the CountedIngredients
                                int count = countedIngredient.getCount();
                                boolean hasEnoughOfThis = false;
                                for (int h = 0; h <= 3; h++) // Check all the slots if there is enough of the CountedIngredient
                                {
                                    if (itemHandler.getStackInSlot(h).getItem().equals(countedIngredient.getItems()[0].getItem()))
                                    {
                                        if (itemHandler.getStackInSlot(h).getCount() >= count)
                                        {
                                            hasEnoughOfThis = true;
                                            amountsToBeSubstracted[h] = count;
                                            break;
                                        }
                                    }
                                }
                                if (!hasEnoughOfThis)
                                {
                                    foundRecipe = false;
                                }
                            }
                        }

                        // A matching recipe was found. Now then:
                        if (foundRecipe)
                        {
                            if (!blockEntity.isBurning() && ForgeHooks.getBurnTime(itemHandler.getStackInSlot(4), RecipeType.SMELTING) > 0)
                            {
                                // There is no fuel currently being burned but there is a fuel in the slot that can be started with burning
                                itemHandler.getStackInSlot(4).shrink(1);
                                blockEntity.fuelTime += ForgeHooks.getBurnTime(itemHandler.getStackInSlot(4), RecipeType.SMELTING);
                                blockEntity.maxTimeOfFuel = ForgeHooks.getBurnTime(itemHandler.getStackInSlot(4), RecipeType.SMELTING); // Store the max burn time for progress calculation
                            }

                            blockEntity.processingTime++;

                            if (blockEntity.processingTime >= 60)
                            {
                                if (itemHandler.getStackInSlot(6).isEmpty()) // 6 is output slot
                                {
                                    // In this case a new result itemstack is added with 1 of the result.
                                    // The items have to be subtracted
                                    for (int i = 0; i <= 3; i++)
                                    {
                                        itemHandler.getStackInSlot(i).shrink(amountsToBeSubstracted[i]);
                                    }
                                    itemHandler.setStackInSlot(6, resultingStack);
                                } else if (itemHandler.getStackInSlot(6).getItem() == resultingStack.getItem() && itemHandler.getStackInSlot(6).getCount() + resultingStack.getCount() <= itemHandler.getStackInSlot(6).getMaxStackSize()) {
                                    // In this case the result itemstack is added to what was already there
                                    // The items have to be subtracted
                                    for (int i = 0; i <= 3; i++)
                                    {
                                        itemHandler.getStackInSlot(i).shrink(amountsToBeSubstracted[i]);
                                    }
                                    itemHandler.getStackInSlot(6).grow(resultingStack.getCount());
                                }
                                blockEntity.processingTime = 0;
                                blockEntity.setChanged();
                            }

                            // Successful
                            return;
                        }
                    }
                }
                if (!foundRecipe)
                {
                    // In this case no recipe match was found. (Because otherwise it would have returned.)
                    blockEntity.processingTime = 0; // Resset the processingtime, in case the recipe got for example interrupted halfway through or so.
                }
            });
        } else {
            // The structure was not properly built or was broken down in the process
            blockEntity.processingTime = 0;
            blockEntity.fuelTime = 0;
            blockEntity.maxTimeOfFuel = 0;
        }
    }
}
