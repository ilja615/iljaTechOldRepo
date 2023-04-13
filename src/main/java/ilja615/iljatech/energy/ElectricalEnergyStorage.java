package ilja615.iljatech.energy;

import net.minecraftforge.energy.EnergyStorage;

public abstract class ElectricalEnergyStorage extends EnergyStorage
{
    public ElectricalEnergyStorage(int capacity, int maxTransfer)
    {
        super(capacity, maxTransfer);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extractedEnergy = super.extractEnergy(maxExtract, simulate);
        if(extractedEnergy != 0) {
            onEnergyChanged();
        }

        return extractedEnergy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int receiveEnergy = super.receiveEnergy(maxReceive, simulate);
        if(receiveEnergy != 0) {
            onEnergyChanged();
        }

        return receiveEnergy;
    }

    public int setEnergy(int e)
    {
        this.energy = e;
        return e;
    }

    public abstract void onEnergyChanged();
}
