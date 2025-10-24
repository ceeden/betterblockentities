package betterblockentities.mixin;

/* local */
import betterblockentities.gui.ConfigManager;

/* minecraft */
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

/* mixin */
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
    this whole mixin will probably get removed once we move to baking the sign text into meshes
    as this implementation is not as efficient.
*/
@Mixin(AbstractSignBlockEntityRenderer.class)
public abstract class AbstractSignBlockEntityRendererMixin {
    @Shadow protected abstract void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state);
    @Shadow protected abstract void renderText(BlockPos pos, SignText text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int textLineHeight, int maxTextWidth, boolean front);

    /* to update just goto the AbstractSignBlockEntityRenderer class and IDEA -> View -> Show Bytecode */
    @Inject(method = "render(Lnet/minecraft/block/entity/SignBlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/block/BlockState;Lnet/minecraft/block/AbstractSignBlock;Lnet/minecraft/block/WoodType;Lnet/minecraft/client/model/Model;)V", at = @At("HEAD"), cancellable = true)
    private void render(SignBlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BlockState state, AbstractSignBlock block, WoodType woodType, Model model, CallbackInfo ci) {
        if (!ConfigManager.CONFIG.optimize_signs || !ConfigManager.CONFIG.master_optimize) return;

        ci.cancel();

        SignText frontText = blockEntity.getFrontText();
        SignText backText = blockEntity.getBackText();

        /* sanity check */
        if (frontText == null || backText == null) return;

        /* check if we have text */
        boolean hasTextFront = hasText(frontText.getMessages(false));
        boolean hasTextBack = hasText(backText.getMessages(false));

        /* if no text then don't render */
        if (!hasTextFront && !hasTextBack) return;

        matrices.push();
        this.applyTransforms(matrices, -block.getRotationDegrees(state), state);

        if (hasTextFront) this.renderText(blockEntity.getPos(), frontText, matrices, vertexConsumers, light, blockEntity.getTextLineHeight(), blockEntity.getMaxTextWidth(), true);
        if (hasTextBack) this.renderText(blockEntity.getPos(), backText, matrices, vertexConsumers, light, blockEntity.getTextLineHeight(), blockEntity.getMaxTextWidth(), false);

        matrices.pop();
    }

    @Unique
    private boolean hasText(Text[] lines) {
        for (Text line : lines) {
            if (line != null && !line.getString().isEmpty()) return true;
        }
        return false;
    }
}
