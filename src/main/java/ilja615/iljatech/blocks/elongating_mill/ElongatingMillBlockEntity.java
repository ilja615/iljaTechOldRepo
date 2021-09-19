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
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
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
import java.util.Arrays;
import java.util.List;

public class ElongatingMillBlockEntity extends BlockEntity
{
    public LazyOptional<IItemHandlerModifiable> elongatingMillItemStackHandler = LazyOptional.of(() -> new ElongatingMillBlockEntity.ElongatingMillItemStackHandler(this));
    private int processingTime;

    public ElongatingMillBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.ELONGATING_MILL.get(), p_155229_, p_155230_);
    }

    @Nullable
    public List<ElongationRecipeType> getRecipes()
    {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.Types.ELONGATION);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElongatingMillBlockEntity blockEntity)
    {
        blockEntity.processingTime++;
        if (!level.isClientSide && blockEntity.processingTime == 100)
        {
            blockEntity.elongatingMillItemStackHandler.ifPresent(itemHandler ->
            {
                List<ElongationRecipeType> recipes = blockEntity.getRecipes();
                for (ElongationRecipeType r : recipes)
                {
                    System.out.println("Recipe ingredient: " + Arrays.toString(r.ingredient.getItems()) + " Recipe result: " + r.result + " First item present: " + itemHandler.getStackInSlot(0) + " Second item present: " + itemHandler.getStackInSlot(1));
                    if (itemHandler.getStackInSlot(1).isEmpty())
                    {
                        itemHandler.getStackInSlot(0).shrink(1);
                        itemHandler.setStackInSlot(1, r.result);
                    } else if (itemHandler.getStackInSlot(1).getItem() == r.result.getItem() && itemHandler.getStackInSlot(1).getCount() + r.result.getCount() <= itemHandler.getStackInSlot(1).getMaxStackSize()) {
                        // In this case the result itemstack is added to what was already there
                        itemHandler.getStackInSlot(0).shrink(1);
                        itemHandler.getStackInSlot(1).grow(r.result.getCount());
                    }
                    System.out.println("(AFTER CRAFT) Recipe ingredient: " + Arrays.toString(r.ingredient.getItems()) + " Recipe result: " + r.result + " First item present: " + itemHandler.getStackInSlot(0) + " Second item present: " + itemHandler.getStackInSlot(1));
                }
            });
            blockEntity.processingTime = 0;
        }
    }

    private boolean isProcessing() {
        return this.processingTime > 0;
    }

    @Override
    public CompoundTag save(CompoundTag compound)
    {
        super.save(compound);
        elongatingMillItemStackHandler.ifPresent(iItemHandlerModifiable -> ((ItemStackHandler)iItemHandlerModifiable).serializeNBT());
        compound.putInt("ProcessingTime", this.processingTime);
        return compound;
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        elongatingMillItemStackHandler.ifPresent(iItemHandlerModifiable -> ((ItemStackHandler)iItemHandlerModifiable).deserializeNBT(compound));
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
            tile.setChanged();
        }
    }
}
