package com.supermartijn642.wirelesschargers;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.core.render.BlockEntityCustomItemRenderer;
import com.supermartijn642.core.render.CustomRendererBakedModelWrapper;
import com.supermartijn642.wirelesschargers.screen.ChargerScreen;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

/**
 * Created 7/1/2021 by SuperMartijn642
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WirelessChargersClient {

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("wirelesschargers");
        for(ChargerType type : ChargerType.values()){
            // Block entity renderers
            handler.registerCustomBlockEntityRenderer(type::getBlockEntityType, ChargerRenderer::new);
            // Baked item models
            ResourceLocation location = new ModelResourceLocation("wirelesschargers:" + type.getRegistryName(), "inventory");
            handler.registerModelOverwrite(location, CustomRendererBakedModelWrapper::wrap);
            // Item renderer
            handler.registerCustomItemRenderer(type::getItem, () -> new BlockEntityCustomItemRenderer<>(
                true,
                () -> type.createBlockEntity(BlockPos.ZERO, type.getBlock().defaultBlockState()),
                (stack, entity) -> entity.readData(stack.hasTag() ? stack.getTag().getCompound("tileData") : new CompoundTag())
            ));
        }
        // Ring models
        for(ChargerModelType type : ChargerModelType.values())
            handler.registerModel(type.ringModel);
    }

    public static void openChargerScreen(Component title, Level level, BlockPos pos){
        ClientUtils.displayScreen(WidgetScreen.of(new ChargerScreen(title, level, pos)));
    }
}