package trinsdar.thermal_mj.asm.mixins;

import cofh.core.block.TileInventory;
import cofh.thermalexpansion.block.dynamo.TileDynamoBase;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileDynamoBase.class)
public abstract class MixinTileDynamoBase extends TileInventory {

}
