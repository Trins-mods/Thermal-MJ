package trinsdar.thermal_mj.asm.mixins;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjCapabilityHelper;
import cofh.CoFHCore;
import cofh.core.block.TilePowered;
import cofh.core.block.TileReconfigurable;
import cofh.redstoneflux.impl.EnergyStorage;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trinsdar.thermal_mj.MJHelper;

@Mixin(TilePowered.class)
public abstract class MixinTilePowered extends TileReconfigurable implements IMjReceiver, IMjReadable {

    @Shadow
    protected EnergyStorage energyStorage;

    public boolean hasCapability(Capability<?> capability, EnumFacing from) {
        return capability == CapabilityEnergy.ENERGY || capability == MjAPI.CAP_RECEIVER || capability == MjAPI.CAP_CONNECTOR || capability == MjAPI.CAP_READABLE || super.hasCapability(capability, from);
    }

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true, remap = false)
    public <T> T getCaps(Capability<T> capability, EnumFacing from, CallbackInfoReturnable<T> info){
        T cap = null;
        if (capability == MjAPI.CAP_RECEIVER){
            cap = MjAPI.CAP_RECEIVER.cast(this);
        } else if (capability == MjAPI.CAP_READABLE){
            cap = MjAPI.CAP_READABLE.cast(this);
        } else if (capability == MjAPI.CAP_CONNECTOR){
            cap = MjAPI.CAP_CONNECTOR.cast(this);
        }
        if (cap != null){
            info.setReturnValue(cap);
        }
        return cap;
    }

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
    public boolean canConnect(@NotNull IMjConnector other) {
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
