package ilja615.iljatech.blocks.wire;

import ilja615.iljatech.blocks.dynamo.DynamoBlockEntity;
import ilja615.iljatech.energy.ElectricalEnergyStorage;
import ilja615.iljatech.energy.MechanicalPower;
import ilja615.iljatech.init.ModBlockEntityTypes;
import ilja615.iljatech.init.ModProperties;
import ilja615.iljatech.networking.EnergySyncS2CPacket;
import ilja615.iljatech.networking.ModMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public class ElectricalWireBlockEntity extends BlockEntity
{
    public ElectricalWireBlockEntity(BlockPos p_155229_, BlockState p_155230_)
    {
        super(ModBlockEntityTypes.ELECTRICAL_WIRE.get(), p_155229_, p_155230_);
    }

    private final ElectricalEnergyStorage ENERGY_STORAGE = new ElectricalEnergyStorage(16000, 256)
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
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("Electricity", ENERGY_STORAGE.getEnergyStored());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        ENERGY_STORAGE.setEnergy(nbt.getInt("Electricity"));
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

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricalWireBlockEntity thisBlockEntity)
    {
        if (level.isClientSide) return;

        if (level.getBlockState(pos).getBlock() instanceof ElectricalWireBlock)
        {
            ElectricalWireBlock electricalWireBlock = (ElectricalWireBlock)state.getBlock();
            for (BlockPos p :WireState.getConnectionArray(pos, state.getValue(electricalWireBlock.getShapeProperty())))
            {
                if (level.getBlockEntity(p) != null)
                {
                    BlockEntity be = level.getBlockEntity(p);
                    be.getCapability(ForgeCapabilities.ENERGY).ifPresent(iEnergyStorage ->
                    {
                        if (iEnergyStorage.getEnergyStored() > thisBlockEntity.getEnergyStorage().getEnergyStored())
                        {
                            int extractedAmount = iEnergyStorage.extractEnergy(8, false);
                            thisBlockEntity.getEnergyStorage().receiveEnergy(extractedAmount, false);
                        }
                    });

                }
            }
        }
    }
}
