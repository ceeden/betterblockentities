package betterblockentities.mixin;

/* local */
import betterblockentities.gui.ConfigManager;
import betterblockentities.util.BlockEntityManager;

/* minecraft */
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

/* mixin */
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* java/misc */
import java.util.function.Supplier;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin {
    @Shadow @Final private Supplier<LoadedBlockEntityModels> blockEntityModelsGetter;
    @Inject(method = "renderBlockAsEntity", at = @At("HEAD"), cancellable = true)
    public void renderBlockAsEntity(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if (BlockEntityManager.isSupportedBlock(state.getBlock()) && ConfigManager.CONFIG.master_optimize) {
            ci.cancel();
            ((LoadedBlockEntityModels)this.blockEntityModelsGetter.get()).render(state.getBlock(), ItemDisplayContext.NONE, matrices, vertexConsumers, light, overlay);
        }
    }
}
