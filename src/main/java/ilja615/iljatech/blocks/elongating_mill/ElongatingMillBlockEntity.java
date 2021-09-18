package ilja615.iljatech.blocks.elongating_mill;

import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModBlocks;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.init.ModRecipeSerializers;
import ilja615.iljatech.mechanicalpower.MechanicalPower;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.rmi.UnexpectedException;

public class ElongatingMillBlockEntity extends BlockEntity
{
    public NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private final static EmptyHandler EMPTYHANDLER = new EmptyHandler();
    public LazyOptional<IItemHandlerModifiable> elongatingMillItemStackHandler = LazyOptional.of(() -> new ElongatingMillBlockEntity.ElongatingMillItemStackHandler(this));
    private int processingTime;
    protected RecipeWrapper wrapper;

    public ElongatingMillBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.ELONGATING_MILL.get(), p_155229_, p_155230_);
    }

    @Nullable
    public ElongationRecipeType getRecipe() {
        assert level != null;
        if (wrapper == null)
            wrapper = new RecipeWrapper(elongatingMillItemStackHandler.orElseThrow(NullPointerException::new));
        return level.getRecipeManager().getRecipeFor(ModRecipeSerializers.Types.ELONGATION, wrapper, level).orElse(null);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElongatingMillBlockEntity blockEntity)
    {
        boolean flag = blockEntity.isProcessing();
        boolean flag1 = false;
        if (blockEntity.isProcessing())
        {
            if (state.hasProperty(ModProperties.MECHANICAL_POWER))
            {
                if (((MechanicalPower)state.getValue(ModProperties.MECHANICAL_POWER)).isSpinning())
                {
                    --blockEntity.processingTime;
                }
            }
            if (blockEntity.processingTime == 0)
            {
                // Now the process is finished.
                ElongationRecipeType recipe = blockEntity.getRecipe();
                assert recipe != null;
                if (recipe.ingredient.getItems()[0].getItem() == blockEntity.items.get(0).getItem())
                {
                    blockEntity.items.get(0).shrink(1);
                    blockEntity.items.set(1, recipe.result);
                }
            }
        }
        if (!level.isClientSide) {
            ItemStack itemstack = blockEntity.items.get(0);
            if (!blockEntity.isProcessing())
            {
                blockEntity.processingTime = itemstack.isEmpty() ? 0 : 60;
                if (blockEntity.isProcessing())
                {
                    flag1 = true;
                    if (!itemstack.isEmpty()) {
                        Item item = itemstack.getItem();
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            blockEntity.items.set(0, itemstack.getContainerItem());
                        }
                    }
                }
            }
            if (flag != blockEntity.isProcessing()) {
                flag1 = true;
            }
        }
        if (flag1) {
            blockEntity.setChanged();
        }
    }

    private boolean isProcessing() {
        return this.processingTime > 0;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public CompoundTag save(CompoundTag compound)
    {
        super.save(compound);
        ContainerHelper.saveAllItems(compound, this.items);
        compound.putInt("ProcessingTime", this.processingTime);
        return compound;
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        this.items = NonNullList.withSize(this.items.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.items);
        elongatingMillItemStackHandler.ifPresent(h ->
        {
            for (int i = 0; i < h.getSlots(); i++)
            {
                h.setStackInSlot(i, items.get(i));
            }
        });
        this.processingTime = compound.getInt("ProcessingTime");
    }

    @Override
    public void invalidateCaps()
    {
        this.elongatingMillItemStackHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction direction)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return elongatingMillItemStackHandler.cast();
        }
        return super.getCapability(capability, direction);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (elongatingMillItemStackHandler != null) {
            elongatingMillItemStackHandler.invalidate();
        }
    }

    private class ElongatingMillItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        private final ElongatingMillBlockEntity tile;

        public ElongatingMillItemStackHandler(ElongatingMillBlockEntity te)
        {
            super(2);
            tile = te;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            tile.items.set(slot, this.stacks.get(slot));
            tile.setChanged();
        }
    }
}
