package trinsdar.thermal_mj.asm.mixins;

import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjCapabilityHelper;
import cofh.core.block.TilePowered;
import cofh.core.block.TileReconfigurable;
import cofh.redstoneflux.impl.EnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import trinsdar.thermal_mj.MJHelper;

@Mixin(TilePowered.class)
public abstract class MixinTilePowered extends TileReconfigurable implements IMjReceiver, IMjReadable {

    @Shadow
    protected EnergyStorage energyStorage;

    @Override
    public long getPowerRequested() {
        return MJHelper.rfToMicro(Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()));
    }

    @Override
    public long receivePower(long microJoules, boolean simulate) {
        return microJoules - MJHelper.rfToMicro(energyStorage.receiveEnergy(MJHelper.microToRf(microJoules), simulate));
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public long getStored(){
        return MJHelper.rfToMicro(energyStorage.getEnergyStored());
    }

    @Override
    public long getCapacity(){
        return MJHelper.rfToMicro(energyStorage.getMaxEnergyStored());
    }


}
