package trinsdar.thermal_mj.asm.mixins;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.thermaldynamics.duct.Duct;
import cofh.thermaldynamics.duct.energy.DuctUnitEnergy;
import cofh.thermaldynamics.duct.energy.GridEnergy;
import cofh.thermaldynamics.duct.tiles.DuctUnit;
import cofh.thermaldynamics.duct.tiles.TileGrid;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import trinsdar.thermal_mj.MJHelper;

@Mixin(DuctUnitEnergy.class)
public abstract class MixinDuctUnitEnergy extends DuctUnit<DuctUnitEnergy, GridEnergy, IEnergyReceiver> implements IMjReceiver, IMjReadable {
    public MixinDuctUnitEnergy(TileGrid parent, Duct duct) {
        super(parent, duct);
    }

    @Override
    public boolean canConnect(@NotNull IMjConnector other) {
        return true;
    }

    @Override
    public long receivePower(long microJoules, boolean simulate) {
        return grid != null ? microJoules - MJHelper.rfToMicro(grid.receiveEnergy(MJHelper.microToRf(microJoules), simulate)) : 0;
    }

    @Override
    public long getPowerRequested() {
        return grid != null ? MJHelper.rfToMicro(Math.min(grid.myStorage.getMaxReceive(), grid.myStorage.getMaxEnergyStored() - grid.myStorage.getEnergyStored())) : 0;
    }

    @Override
    public long getStored() {
        return grid != null ? MJHelper.rfToMicro(grid.myStorage.getEnergyStored()) : 0;
    }

    @Override
    public long getCapacity() {
        return grid != null ? MJHelper.rfToMicro(grid.myStorage.getMaxEnergyStored()) : 0;
    }
}
