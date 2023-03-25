package com.supermartijn642.wirelesschargers.mixin;

import com.supermartijn642.wirelesschargers.ChargerBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 08/03/2023 by SuperMartijn642
 */
@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {

    @Inject(
        method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void shouldConnectTo(BlockState blockState, Direction direction, CallbackInfoReturnable<Boolean> ci){
        if(blockState.getBlock() instanceof ChargerBlock)
            ci.setReturnValue(true);
    }
}
