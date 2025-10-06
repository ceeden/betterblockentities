package betterblockentities.mixin;

import betterblockentities.ModelLoader;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BellBlockEntityRenderer.class)
public class BellBlockEntityRendererMixin
{
    @Shadow
    @Final
    public static SpriteIdentifier BELL_BODY_TEXTURE;

    @Inject(method = "render", at = @At("TAIL"))
    private <T extends BlockEntity> void onRender(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci)
    {
        ModelLoader.loadModels();

        BlockState state = blockEntity.getWorld().getBlockState(blockEntity.getPos());

        Attachment attachment = state.get(BellBlock.ATTACHMENT);
        Direction facing = state.get(BellBlock.FACING);

        matrices.push();

        matrices.translate(0.5, 0.0, 0.5);
        if (attachment != Attachment.FLOOR) {
            switch (facing) {
                case NORTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90f));
                case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90f));
                case WEST  -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
                case EAST  -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0f));
            }
        }
        else {
            switch (facing) {
                case NORTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0f));
                case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
                case WEST  -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90f));
                case EAST  -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90f));
            }
        }
        matrices.translate(-0.5, 0.0, -0.5);

        List<BakedQuad> quads = null;
        switch (attachment) {
            case FLOOR -> quads = ModelLoader.bell_floor_quads;
            case CEILING -> quads = ModelLoader.bell_ceiling_quads;
            case SINGLE_WALL -> quads = ModelLoader.bell_wall_quads;
            case DOUBLE_WALL -> quads = ModelLoader.bell_between_walls_quads;
        }

        VertexConsumer vertexConsumer = this.BELL_BODY_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        if (quads != null)
            for (BakedQuad quad : quads)
                vertexConsumer.quad(matrices.peek(), quad, 1f, 1f, 1f, 1f, light, overlay);
        matrices.pop();
    }
}
