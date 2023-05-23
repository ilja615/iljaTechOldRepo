package ilja615.iljatech.blocks.battery_box;

import ilja615.iljatech.blocks.wire.ElectricalWireBlock;
import ilja615.iljatech.blocks.wire.WireBlock;
import ilja615.iljatech.blocks.wire.WireState;
import ilja615.iljatech.energy.ElectricalEnergyStorage;
import ilja615.iljatech.energy.MechanicalPower;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.networking.EnergySyncS2CPacket;
import ilja615.iljatech.networking.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BatteryBoxBlockEntity extends BlockEntity implements MenuProvider
{
    private Component customName;
    public NonNullList<ItemStack> chestContents = NonNullList.withSize(6, ItemStack.EMPTY);
    protected int numPlayersUsing;
    public LazyOptional<IItemHandlerModifiable> batteryBoxItemStackHandler = LazyOptional.of(() -> new BatteryBoxItemStackHandler(this));

    public BatteryBoxBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.BATTERY_BOX.get(), p_155229_, p_155230_);
    }

    private final ElectricalEnergyStorage ENERGY_STORAGE = new ElectricalEnergyStorage(32000, 256)
    {
        @Override
        public void onEnergyChanged()
        {
            setChanged();
            ModMessages.sendToClients(new EnergySyncS2CPacket(this.energy, getBlockPos()));
        }
    };
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    public IEnergyStorage getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public void setEnergyLevel(int energy) {
        this.ENERGY_STORAGE.setEnergy(energy);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
        this.batteryBoxItemStackHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("Electricity", ENERGY_STORAGE.getEnergyStored());
        ContainerHelper.saveAllItems(nbt, this.chestContents);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        ENERGY_STORAGE.setEnergy(nbt.getInt("Electricity"));
        this.chestContents = NonNullList.withSize(this.chestContents.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.chestContents);
        batteryBoxItemStackHandler.ifPresent(h ->
        {
            for (int i = 0; i < h.getSlots(); i++)
            {
                h.setStackInSlot(i, chestContents.get(i));
            }
        });
        super.load(nbt);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap);
    }

//    @Override
//    public CompoundTag getUpdateTag() {
//        super.getUpdateTag();
//        CompoundTag tag = new CompoundTag();
//        //Write your data into the tag
//        ENERGY_STORAGE.setEnergy(tag.getInt("Electricity"));
//        ContainerHelper.saveAllItems(tag, this.chestContents);
//        return tag;
//    }
//
//    @Override
//    public void handleUpdateTag(CompoundTag tag)
//    {
//        super.handleUpdateTag(tag);
//        ENERGY_STORAGE.setEnergy(tag.getInt("Electricity"));
//        this.chestContents = NonNullList.withSize(this.chestContents.size(), ItemStack.EMPTY);
//        ContainerHelper.loadAllItems(tag, this.chestContents);
//        batteryBoxItemStackHandler.ifPresent(h ->
//        {
//            for (int i = 0; i < h.getSlots(); i++)
//            {
//                h.setStackInSlot(i, chestContents.get(i));
//            }
//        });
//    }
//
//    @Override
//    public Packet<ClientGamePacketListener> getUpdatePacket() {
//        // Will get tag from #getUpdateTag
//        return ClientboundBlockEntityDataPacket.create(this);
//    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player)
    {
        return new BatteryBoxContainer(id, playerInventory, this);
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
        return Component.translatable("container.battery_box");
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
        if (block instanceof BatteryBoxBlock) {
            this.level.blockEvent(this.worldPosition, block, 1, this.numPlayersUsing);
            this.level.updateNeighborsAt(this.worldPosition, block);
        }
    }

    public static int getPlayersUsing(BlockGetter reader, BlockPos pos) {
        BlockState blockState = reader.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity tileEntity = reader.getBlockEntity(pos);
            if (tileEntity instanceof BatteryBoxBlockEntity) {
                return ((BatteryBoxBlockEntity) tileEntity).numPlayersUsing;
            }
        }
        return 0;
    }

    public static void swapContent(BatteryBoxBlockEntity tileEntity, BatteryBoxBlockEntity otherTileEntity) {
        NonNullList<ItemStack> list = tileEntity.getItems();
        tileEntity.setItems(otherTileEntity.getItems());
        otherTileEntity.setItems(list);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction direction)
    {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return batteryBoxItemStackHandler.cast();
        }
        return super.getCapability(capability, direction);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (batteryBoxItemStackHandler != null) {
            batteryBoxItemStackHandler.invalidate();
        }
    }
    
    private class BatteryBoxItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        private final BatteryBoxBlockEntity blockEntity;

        public BatteryBoxItemStackHandler(BatteryBoxBlockEntity be)
        {
            super(6);
            blockEntity = be;
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            if (slot < 6) blockEntity.chestContents.set(slot, this.stacks.get(slot));
            blockEntity.setChanged();
        }
    }

    public void setItemStackAndSaveAndSync(int slot, ItemStack itemStack)
    {
        this.chestContents.set(slot, itemStack);
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BatteryBoxBlockEntity thisBlockEntity)
    {
        if (level.isClientSide) return;

        // The battery box will extract the energy from nearby sources
        for (Direction d : Direction.values())
        {
            BlockPos p = pos.relative(d);
            BlockEntity be = level.getBlockEntity(p);
            be.getCapability(ForgeCapabilities.ENERGY).ifPresent(iEnergyStorage ->
            {
                if (iEnergyStorage.getEnergyStored() > thisBlockEntity.getEnergyStorage().getEnergyStored())
                {
                    int extractedAmount = iEnergyStorage.extractEnergy(32, false);
                    thisBlockEntity.getEnergyStorage().receiveEnergy(extractedAmount, false);
                }
            });
        }

        // The battery box will then send it through a wire if possible
        for (Direction d : Direction.values())
        {
            BlockPos p = pos.relative(d);
            if (level.getBlockState(p).getBlock() instanceof ElectricalWireBlock)
            {
                ((ElectricalWireBlock)state.getBlock());
            }
        }
    }
}
