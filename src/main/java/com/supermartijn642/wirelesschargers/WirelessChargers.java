package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.item.CreativeItemGroup;
import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.registry.RegistrationHandler;
import com.supermartijn642.wirelesschargers.generators.*;
import com.supermartijn642.wirelesschargers.packets.CycleRedstoneModePacket;
import com.supermartijn642.wirelesschargers.packets.ToggleHighlightAreaPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

/**
 * Created 7/7/2020 by SuperMartijn642
 */
@Mod("wirelesschargers")
public class WirelessChargers {

    public static final PacketChannel CHANNEL = PacketChannel.create("wirelesschargers");
    public static final CreativeItemGroup GROUP = CreativeItemGroup.create("wirelesschargers", ChargerType.ADVANCED_WIRELESS_BLOCK_CHARGER::getItem);

    public WirelessChargers(){
        CHANNEL.registerMessage(ToggleHighlightAreaPacket.class, ToggleHighlightAreaPacket::new, true);
        CHANNEL.registerMessage(CycleRedstoneModePacket.class, CycleRedstoneModePacket::new, true);

        register();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> WirelessChargersClient::register);
        registerGenerators();
    }

    private static void register(){
        RegistrationHandler handler = RegistrationHandler.get("wirelesschargers");
        for(ChargerType type : ChargerType.values()){
            handler.registerBlockCallback(type::registerBlock);
            handler.registerBlockEntityTypeCallback(type::registerBlockEntity);
            handler.registerItemCallback(type::registerItem);
        }
    }

    public static void registerGenerators(){
        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get("wirelesschargers");
        handler.addGenerator(ChargerModelGenerator::new);
        handler.addGenerator(ChargerBlockStateGenerator::new);
        handler.addGenerator(ChargerLanguageGenerator::new);
        handler.addGenerator(ChargerLootTableGenerator::new);
        handler.addGenerator(ChargerRecipeGenerator::new);
        handler.addGenerator(ChargerTagGenerator::new);
        handler.addGenerator(ChargerAdvancementGenerator::new);
    }
}
