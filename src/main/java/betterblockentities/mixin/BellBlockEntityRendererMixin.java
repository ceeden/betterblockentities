package betterblockentities.mixin;

import betterblockentities.ModelLoader;
import betterblockentities.helpers.BellRenderStateAccessor;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BellBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BellBlockEntityRenderer.class)
public class BellBlockEntityRendererMixin
{
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void updateState(BellBlockEntity bell, BellBlockEntityRenderState renderState, float f, Vec3d vec3d, @Nullable ModelCommandRenderer.CrumblingOverlayCommand overlay, CallbackInfo ci)
    {
        BlockState blockState = bell.getCachedState();

        ((BellRenderStateAccessor)renderState).setMountAttachment(blockState.get(BellBlock.ATTACHMENT));
        ((BellRenderStateAccessor)renderState).setMountFacing(blockState.get(BellBlock.FACING));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderInject(BellBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera, CallbackInfo ci)
    {
        BellRenderStateAccessor accessor = (BellRenderStateAccessor) (Object) state;
        Attachment attachment = accessor.getMountAttachment();
        Direction facing = accessor.getMountFacing();

        ModelLoader.loadModels();

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

        BlockStateModel model = null;
        switch (attachment) {
            case FLOOR -> model = ModelLoader.bell_floor;
            case CEILING -> model = ModelLoader.bell_ceiling;
            case SINGLE_WALL -> model = ModelLoader.bell_wall;
            case DOUBLE_WALL -> model = ModelLoader.bell_between_walls;
        }

        queue.submitBlockStateModel(matrices, RenderLayer.getSolid(), model, 0.1f, 0.1f, 0.1f, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }
}