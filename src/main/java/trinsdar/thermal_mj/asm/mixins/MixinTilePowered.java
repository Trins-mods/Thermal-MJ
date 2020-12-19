package trinsdar.thermal_mj.asm.mixins;

import buildcraft.api.mj.IMjReceiver;
import cofh.core.block.TilePowered;
import cofh.core.block.TileReconfigurable;
import net.minecraftforge.energy.EnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TilePowered.class)
public abstract class MixinTilePowered extends TileReconfigurable implements IMjReceiver {
    @Shadow
    protected EnergyStorage energyStorage;

    @Override
    public long getPowerRequested() {
        return 0;
    }

    @Override
    public long receivePower(long microJoules, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
