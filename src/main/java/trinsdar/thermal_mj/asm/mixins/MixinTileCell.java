package trinsdar.thermal_mj.asm.mixins;

import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import cofh.CoFHCore;
import cofh.core.block.TilePowered;
import cofh.thermalexpansion.block.storage.TileCell;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trinsdar.thermal_mj.MJHelper;

@Mixin(TileCell.class)
public abstract class MixinTileCell extends TilePowered implements IMjPassiveProvider, IMjReceiver, IMjReadable {
    public long extractPower(long min, long max, boolean simulate){
        int max1 = MJHelper.microToRf(max);
        int actualMin = energyStorage.extractEnergy(max1, true);
        if (actualMin < min) {
            return 0;
        }
        return MJHelper.rfToMicro(energyStorage.extractEnergy(max1, simulate));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing from) {
        return capability == CapabilityEnergy.ENERGY || capability == MjAPI.CAP_RECEIVER || capability == MjAPI.CAP_CONNECTOR || capability == MjAPI.CAP_PASSIVE_PROVIDER || capability == MjAPI.CAP_READABLE || super.hasCapability(capability, from);
    }

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true, remap = false)
    public <T> T getCaps(Capability<T> capability, EnumFacing from, CallbackInfoReturnable<T> info){
        T cap = null;
        if (capability == MjAPI.CAP_RECEIVER){
            if (from == null || sideCache[from.ordinal()] == 1){
                cap = MjAPI.CAP_RECEIVER.cast(this);
            } else if (sideCache[from.ordinal()] != 1){
                info.setReturnValue(null);
                return null;
            }

        } else if (capability == MjAPI.CAP_READABLE){
            cap = MjAPI.CAP_READABLE.cast(this);
        } else if (capability == MjAPI.CAP_CONNECTOR){
            cap = MjAPI.CAP_CONNECTOR.cast(this);
        } else if (capability == MjAPI.CAP_PASSIVE_PROVIDER){
            if (from == null || sideCache[from.ordinal()] == 2){
                cap = MjAPI.CAP_PASSIVE_PROVIDER.cast(this);
            } else if (sideCache[from.ordinal()] != 2){
                info.setReturnValue(null);
                return null;
            }
        }
        if (cap != null){
            info.setReturnValue(cap);
        }
        return cap;
    }
}
