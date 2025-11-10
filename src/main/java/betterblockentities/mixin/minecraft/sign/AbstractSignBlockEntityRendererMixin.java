package betterblockentities.mixin.minecraft.sign;

/* local */
import betterblockentities.gui.ConfigManager;

/* minecraft */
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.SignBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
    this whole mixin will probably get removed once we move to baking the sign text into meshes
    as this implementation is not as efficient
*/
@Mixin(AbstractSignBlockEntityRenderer.class)
public abstract class AbstractSignBlockEntityRendererMixin {
    @Shadow protected abstract void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state);
    @Shadow protected abstract void renderText(SignBlockEntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, boolean front);

    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/state/SignBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    public void render(SignBlockEntityRenderState state, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!ConfigManager.CONFIG.optimize_signs || !ConfigManager.CONFIG.master_optimize) return;

        ci.cancel();

        /* sanity check */
        if (state.frontText == null || state.backText == null) return;

        /* check if we have text */
        boolean hasTextFront = hasText(state.frontText.getMessages(false));
        boolean hasTextBack = hasText(state.backText.getMessages(false));

        /* if no text then don't render */
        if (!hasTextFront && !hasTextBack) return;

        BlockState blockState = state.blockState;
        AbstractSignBlock block = (AbstractSignBlock) blockState.getBlock();

        matrixStack.push();
        this.applyTransforms(matrixStack, -block.getRotationDegrees(blockState), blockState);

        if (hasTextFront) this.renderText(state, matrixStack, orderedRenderCommandQueue, true);
        if (hasTextBack)  this.renderText(state, matrixStack, orderedRenderCommandQueue, false);

        matrixStack.pop();
    }

    @Unique
    private boolean hasText(Text[] lines) {
        for (Text line : lines) {
            if (line != null && !line.getString().isEmpty()) return true;
        }
        return false;
    }
}
