package ilja615.iljatech.blocks.burner;

import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.util.interactions.Heat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;

public class BurnerBlockEntity extends BlockEntity
{
    public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final static EmptyHandler EMPTYHANDLER = new EmptyHandler();
    public LazyOptional<IItemHandlerModifiable> burnerItemStackHandler = LazyOptional.of(() -> new BurnerBlockEntity.BurnerItemStackHandler(this));
    private int burnTime;

    public BurnerBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.BURNER.get(), p_155229_, p_155230_);
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BurnerBlockEntity blockEntity)
    {
        boolean flag = blockEntity.isBurning();
        boolean flag1 = false;
        if (blockEntity.isBurning()) {
            --blockEntity.burnTime;
            if (blockEntity.burnTime % 100 == 0)
            {
                Heat.emitHeat(level, pos);
            }
        }
        if (!level.isClientSide) {
            ItemStack itemstack = blockEntity.items.get(0);
            if (!blockEntity.isBurning())
            {
                blockEntity.burnTime = blockEntity.getBurnTime(itemstack);
                if (blockEntity.isBurning())
                {
                    flag1 = true;
                    if (itemstack.hasContainerItem())
                        blockEntity.items.set(0, itemstack.getContainerItem());
                    else
                    if (!itemstack.isEmpty()) {
                        Item item = itemstack.getItem();
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            blockEntity.items.set(0, itemstack.getContainerItem());
                        }
                    }
                }
            }
            if (flag != blockEntity.isBurning()) {
                flag1 = true;
                level.setBlock(pos, level.getBlockState(pos).setValue(BurnerBlock.LIT, Boolean.valueOf(blockEntity.isBurning())), 3);
            }
        }
        if (flag1) {
            blockEntity.setChanged();
        }
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void saveAdditional(CompoundTag compound)
    {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, this.items);
        compound.putInt("BurnTime", this.burnTime);
    }

    @Override
    public void load(CompoundTag compound)
    {
        super.load(compound);
        this.items = NonNullList.withSize(this.items.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.items);
        burnerItemStackHandler.ifPresent(h ->
        {
            for (int i = 0; i < h.getSlots(); i++)
            {
                h.setStackInSlot(i, items.get(i));
            }
        });
        this.burnTime = compound.getInt("BurnTime");
    }

    @Override
    public void invalidateCaps()
    {
        this.burnerItemStackHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction direction)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return burnerItemStackHandler.cast();
        }
        return super.getCapability(capability, direction);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (burnerItemStackHandler != null) {
            burnerItemStackHandler.invalidate();
        }
    }

    private class BurnerItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        private final BurnerBlockEntity tile;

        public BurnerItemStackHandler(BurnerBlockEntity te)
        {
            super(1);
            tile = te;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            tile.items.set(slot, this.stacks.get(slot));
            tile.setChanged();
        }
    }

    protected int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
        }
    }
}
