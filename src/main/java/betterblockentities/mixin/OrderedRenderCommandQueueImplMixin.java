package betterblockentities.mixin;

/* local */
import betterblockentities.gui.ConfigManager;
import betterblockentities.util.BlockEntityManager;

/* minecraft */
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
    quick and dirty fix for chest minecarts and endermen holding any supported block where there is two BEs rendered
    just removes this line in BatchingRenderCommandQueue -> submitBlock:
    -this.blockCommands.add(new OrderedRenderCommandQueueImpl.BlockCommand(matrices.peek().copy(), state, light, overlay, outlineColor));
    which loads a predefined model (from the block´s blockstate json) aka our defined chest model in our pack
*/

@Mixin(OrderedRenderCommandQueueImpl.class)
public class OrderedRenderCommandQueueImplMixin {
    @Inject(method = "submitBlock", at = @At("HEAD"), cancellable = true)
    public void submitBlock(MatrixStack matrices, BlockState state, int light, int overlay, int outlineColor, CallbackInfo ci) {
        if (BlockEntityManager.isSupportedBlock(state.getBlock()) && ConfigManager.CONFIG.master_optimize) {
            ci.cancel();
            ((LoadedBlockEntityModels)MinecraftClient.getInstance().getBakedModelManager().getBlockEntityModelsSupplier().get()).render(state.getBlock(), ItemDisplayContext.NONE, matrices, (OrderedRenderCommandQueue)this, light, OverlayTexture.DEFAULT_UV, 0);
        }
    }
}
