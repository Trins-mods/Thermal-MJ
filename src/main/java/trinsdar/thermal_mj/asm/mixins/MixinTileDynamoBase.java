package trinsdar.thermal_mj.asm.mixins;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReadable;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import cofh.CoFHCore;
import cofh.core.block.TileInventory;
import cofh.redstoneflux.impl.EnergyStorage;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trinsdar.thermal_mj.MJHelper;

@Mixin(TileDynamoBase.class)
public abstract class MixinTileDynamoBase extends TileInventory implements IMjPassiveProvider, IMjReadable {
    @Shadow
    protected EnergyStorage energyStorage;

    @Shadow
    public abstract int getFacing();

    @Inject(method = "getCapability", at = @At("HEAD"), cancellable = true, remap = false)
    public <T> T getCaps(Capability<T> capability, EnumFacing from, CallbackInfoReturnable<T> info){
        T cap = null;
        if (capability == MjAPI.CAP_READABLE){
            cap = MjAPI.CAP_READABLE.cast(this);
        } else if (capability == MjAPI.CAP_CONNECTOR){
            cap = MjAPI.CAP_CONNECTOR.cast(this);
        } else if (capability == MjAPI.CAP_PASSIVE_PROVIDER){
            if (from == null || from.ordinal() == this.getFacing()){
                cap = MjAPI.CAP_PASSIVE_PROVIDER.cast(this);
            }
        }
        if (cap != null){
            info.setReturnValue(cap);
        }
        return cap;
    }

    public IMjReceiver getReceiverToPower(TileEntity tile, EnumFacing side) {
        if (tile == null) return null;
        IMjReceiver rec = tile.getCapability(MjAPI.CAP_RECEIVER, side.getOpposite());
        if (rec != null && rec.canConnect(this) && this.canConnect(rec)) {
            return rec;
        } else {
            return null;
        }
    }

    private long getPowerToExtract(EnumFacing currentDirection) {
        TileEntity tile = world.getTileEntity(getPos().offset(currentDirection));
        if (tile == null) return 0;
        IMjReceiver receiver = getReceiverToPower(tile, currentDirection);
        if (receiver == null) {
            return 0;
        }
        // Pulsed power
        return extractPower(0, receiver.getPowerRequested(), false);
        // TODO: Use this:
        // return extractPower(receiver.getMinPowerReceived(), receiver.getMaxPowerReceived(), false);

        // Constant power
        // return extractEnergy(0, getActualOutput(), false); // Uncomment for constant power
    }

    @Inject(method = "transferEnergy", at = @At("HEAD"), remap = false)
    private void sendPower(CallbackInfo info) {
        EnumFacing facing = EnumFacing.getFront(this.getFacing());
        TileEntity tile = world.getTileEntity(getPos().offset(facing));
        if (tile == null) {
            CoFHCore.LOG.info("tile null");
            return;
        }
        IMjReceiver receiver = getReceiverToPower(tile, facing);
        if (receiver != null) {
            long extracted = getPowerToExtract(facing);
            if (extracted > 0) {
                long excess = receiver.receivePower(extracted, false);
                //extractPower(extracted - excess, extracted - excess, false); // Comment out for constant power
                // currentOutput = extractEnergy(0, needed, true); // Uncomment for constant power
            }
        }
    }

    @Override
    public boolean canConnect(@NotNull IMjConnector other) {
        return world.getTileEntity(pos.offset(EnumFacing.getFront(getFacing()))) == other;
    }

    @Override
    public long extractPower(long min, long max, boolean simulate){
        int max1 = MJHelper.microToRf(max);
        int actualMin = energyStorage.extractEnergy(max1, true);
        if (actualMin < min) {
            return 0;
        }
        return MJHelper.rfToMicro(energyStorage.extractEnergy(max1, simulate));
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
