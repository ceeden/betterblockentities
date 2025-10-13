package betterblockentities.mixin;

/* minecraft */
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.SignBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
    this whole mixin will probably get removed once we move to baking the sign text into meshes
    as this implementation is not as efficient.
*/
@Mixin(AbstractSignBlockEntityRenderer.class)
public abstract class AbstractSignBlockEntityRendererMixin {
    @Shadow
    protected abstract void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state);

    @Shadow
    protected abstract void renderText(SignBlockEntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, boolean front);

    /* to update just goto the AbstractSignBlockEntityRenderer class and IDEA -> View -> Show Bytecode */
    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/state/SignBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    public void render(SignBlockEntityRenderState signBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        ci.cancel();

        BlockState blockState = signBlockEntityRenderState.blockState;
        AbstractSignBlock block = (AbstractSignBlock)blockState.getBlock();

        matrixStack.push();
        this.applyTransforms(matrixStack, -block.getRotationDegrees(blockState), blockState);
        this.renderText(signBlockEntityRenderState, matrixStack, orderedRenderCommandQueue, true);
        this.renderText(signBlockEntityRenderState, matrixStack, orderedRenderCommandQueue, false);
        matrixStack.pop();
    }
}
