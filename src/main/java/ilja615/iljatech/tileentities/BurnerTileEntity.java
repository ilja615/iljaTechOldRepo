package ilja615.iljatech.tileentities;

import ilja615.iljatech.blocks.BurnerBlock;
import ilja615.iljatech.entity.GassEntity;
import ilja615.iljatech.init.ModEntities;
import ilja615.iljatech.init.ModTileEntityTypes;
import ilja615.iljatech.util.CraftingInventoryWrapper;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;
import java.util.Optional;

public class BurnerTileEntity extends TileEntity implements ITickableTileEntity
{
    public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final static EmptyHandler EMPTYHANDLER = new EmptyHandler();
    public LazyOptional<IItemHandlerModifiable> burnerItemStackHandler = LazyOptional.of(() -> new BurnerTileEntity.BurnerItemStackHandler(this));
    private int burnTime;

    public BurnerTileEntity(TileEntityType<?> tileEntityTypeIn) { super(tileEntityTypeIn); }
    public BurnerTileEntity() { this(ModTileEntityTypes.BURNER.get()); }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void tick()
    {
        boolean flag = this.isBurning();
        boolean flag1 = false;
        if (this.isBurning()) {
            --this.burnTime;
            if (this.burnTime % 100 == 0)
            {
                this.emitHeat();
            }
        }
        System.out.println("Burner tick. Burning: "+this.isBurning()+". burnTime: "+this.burnTime);
        if (!this.world.isRemote) {
            ItemStack itemstack = this.items.get(0);
            if (!this.isBurning())
            {
                this.burnTime = getBurnTime(itemstack);
                if (this.isBurning())
                {
                    flag1 = true;
                    if (itemstack.hasContainerItem())
                        this.items.set(0, itemstack.getContainerItem());
                    else
                    if (!itemstack.isEmpty()) {
                        Item item = itemstack.getItem();
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            this.items.set(0, itemstack.getContainerItem());
                        }
                    }
                }
            }
            if (flag != this.isBurning()) {
                flag1 = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BurnerBlock.LIT, Boolean.valueOf(this.isBurning())), 3);
            }
        }
        if (flag1) {
            this.markDirty();
        }
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, this.items);
        compound.putInt("BurnTime", this.burnTime);
        return compound;
    }

    @Override
    public void read(BlockState blockState, CompoundNBT compound)
    {
        super.read(blockState, compound);
        this.items = NonNullList.withSize(this.items.size(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.items);
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
    protected void invalidateCaps()
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
    public void remove() {
        super.remove();
        if (burnerItemStackHandler != null) {
            burnerItemStackHandler.invalidate();
        }
    }

    private void emitHeat()
    {
        if (!this.world.isRemote) {
            BlockState state = this.world.getBlockState(this.pos.up());
            if (this.pos.getY() < this.world.getHeight() - 1 && state.getBlock() == Blocks.CAULDRON) {
                int value = state.get(CauldronBlock.LEVEL) - 1; // The value that the cauldron level would be.
                if (value >= 0) {
                    this.world.setBlockState(this.pos.up(), state.with(CauldronBlock.LEVEL, value));
                    GassEntity gassEntity = ModEntities.STEAM_CLOUD.get().create(this.world);
                    gassEntity.setLocationAndAngles(this.pos.getX() + 0.5f, this.pos.getY() + 1.8f, this.pos.getZ() + 0.5f, 0.0f, 0.0F);
                    this.world.addEntity(gassEntity);
                }
            }
        }
    }

    private class BurnerItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        private final BurnerTileEntity tile;

        public BurnerItemStackHandler(BurnerTileEntity te)
        {
            super(1);
            tile = te;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            tile.items.set(slot, this.stacks.get(slot));
            tile.markDirty();
        }
    }

    protected int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return ForgeHooks.getBurnTime(fuel);
        }
    }
}
