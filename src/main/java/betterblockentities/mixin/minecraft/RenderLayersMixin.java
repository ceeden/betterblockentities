package betterblockentities.mixin.minecraft;

/* minecraft */
import betterblockentities.gui.ConfigManager;
import net.minecraft.block.*;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayers;

/* mixin */
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayers.class)
public class RenderLayersMixin
{
    /* force some of the supported blocks to the TRANSLUCENT renderlayer inorder to get proper rendering */
    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    private static void ForceBlockLayer(BlockState state, CallbackInfoReturnable<BlockRenderLayer> cir) {
        if (!ConfigManager.CONFIG.master_optimize) return;
        if (state.getBlock() instanceof ShulkerBoxBlock ||
                state.getBlock() instanceof DecoratedPotBlock ||
                state.getBlock() instanceof HangingSignBlock  ||
                state.getBlock() instanceof WallHangingSignBlock)
            cir.setReturnValue(BlockRenderLayer.TRANSLUCENT);
    }
}

