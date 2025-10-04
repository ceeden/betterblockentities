package betterblockentities.mixin;

import net.minecraft.block.*;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;


@Mixin(RenderLayers.class)
public class RenderLayersMixin
{
    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    private static void ForceBlockLayer(BlockState state, CallbackInfoReturnable<BlockRenderLayer> cir)
    {
        if (state.getBlock() instanceof ShulkerBoxBlock || state.getBlock() instanceof DecoratedPotBlock)
            cir.setReturnValue(BlockRenderLayer.TRANSLUCENT);
    }
}

