package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.wirelesschargers.screen.ChargerScreen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
public class WirelessChargersClient {

    private static final Map<ChargerType,BakedModel> RING_MODELS = new EnumMap<>(ChargerType.class);

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("wirelesschargers");
        for(ChargerType type : ChargerType.values()){
            // Block entity renderers
            handler.registerCustomBlockEntityRenderer(type::getBlockEntityType, ChargerRenderer::new);
            handler.registerBlockSpecialModelRenderer(type::getBlock, () -> new ChargerSpecialModelRenderer(type));
            // Ring models
            handler.registerModelConsumer(type.modelType.ringModel, model -> RING_MODELS.put(type, model));
        }
        // Special model renderer
        handler.registerSpecialModelRenderer("charger", ChargerSpecialModelRenderer.CODEC);
    }

    public static void openChargerScreen(Component title, Level level, BlockPos pos){
        ClientUtils.displayScreen(WidgetScreen.of(new ChargerScreen(title, level, pos)));
    }

    public static BakedModel getRingModel(ChargerType type){
        return RING_MODELS.get(type);
    }
}
