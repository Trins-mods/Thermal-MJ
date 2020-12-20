package trinsdar.thermal_mj;

import cofh.CoFHCore;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(name = ThermalMJ.NAME, modid = ThermalMJ.MODID, version = ThermalMJ.VERSION, dependencies = ThermalMJ.DEPENDS)
public class ThermalMJ {
    public static final String MODID = "thermal_mj";
    public static final String NAME = "Thermal MJ";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDS = CoFHCore.VERSION_GROUP;

    @Mod.Instance
    public static ThermalMJ instance;

    public static Logger logger;

    public ThermalMJ() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }
}
