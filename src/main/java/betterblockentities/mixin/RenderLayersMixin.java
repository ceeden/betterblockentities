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
    /*
        we need to return BlockRenderLayer.TRANSLUCENT for shulker boxes
        because they have transparent parts in the texture, if we don't
        do this we get z-fighting because the SOLID layer fills
        in the transparent parts
    */
    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    private static void ForceBlockLayer(BlockState state, CallbackInfoReturnable<BlockRenderLayer> cir)
    {
        if (state.getBlock() instanceof ShulkerBoxBlock)
            cir.setReturnValue(BlockRenderLayer.TRANSLUCENT);
    }
}

