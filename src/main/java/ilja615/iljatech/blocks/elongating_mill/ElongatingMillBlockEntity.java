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
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
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

    public int getProcessingTime()
    {
        return processingTime;
    }

    @Nullable
    public List<ElongationRecipeType> getRecipes()
    {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.Types.ELONGATION);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElongatingMillBlockEntity blockEntity)
    {
        blockEntity.elongatingMillItemStackHandler.ifPresent(itemHandler ->
        {
            List<ElongationRecipeType> recipes = blockEntity.getRecipes();
            for (ElongationRecipeType r : recipes)
            {
                ItemStack resultingStack = r.result.copy();
                if (r.ingredient.getItems()[0].isEmpty() || itemHandler.getStackInSlot(0).isEmpty())
                    break;

                if (r.ingredient.getItems()[0].getItem() == itemHandler.getStackInSlot(0).getItem())
                {
                    // A matching recipe was found. Now then:
                    if (state.getValue(ModProperties.MECHANICAL_POWER) != MechanicalPower.OFF)
                    {
                        blockEntity.processingTime++;
                        if (level.isClientSide)
                            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemHandler.getStackInSlot(0)), pos.getX() + 0.25f + 0.5 * level.random.nextFloat(), pos.getY()  + 0.25f + 0.5 * level.random.nextFloat(), pos.getZ()  + 0.25f + 0.5 * level.random.nextFloat(),  - 0.1f + 0.2 * level.random.nextFloat(), 0.1f + 0.2 * level.random.nextFloat(), - 0.1f + 0.2 * level.random.nextFloat());
                    }

                    if (blockEntity.processingTime >= 100)
                    {
                        if (itemHandler.getStackInSlot(1).isEmpty())
                        {
                            // In this case a new result itemstack is added with 1 of the result.
                            itemHandler.getStackInSlot(0).shrink(1);
                            itemHandler.setStackInSlot(1, resultingStack);
                        } else if (itemHandler.getStackInSlot(1).getItem() == resultingStack.getItem() && itemHandler.getStackInSlot(1).getCount() + resultingStack.getCount() <= itemHandler.getStackInSlot(1).getMaxStackSize()) {
                            // In this case the result itemstack is added to what was already there
                            itemHandler.getStackInSlot(0).shrink(1);
                            itemHandler.getStackInSlot(1).grow(resultingStack.getCount());
                        }
                        blockEntity.processingTime = 0;
                        blockEntity.setChanged();
                    }
                    return;
                }
                // In this case no recipe match was found. (Because otherwise it would have returned.)
                blockEntity.processingTime = 0; // Resset the processingtime, in case the recipe got for example interrupted halfway through or so.
            }
        });
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
