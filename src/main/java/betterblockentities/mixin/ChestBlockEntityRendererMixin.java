package betterblockentities.mixin;

/* local */
import betterblockentities.gui.ConfigManager;
import betterblockentities.model.BBEChestBlockModel;

/* minecraft */
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;

/* mixin */
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestBlockEntityRendererMixin
{
    @Shadow @Mutable private ChestBlockModel singleChest;
    @Shadow @Mutable private ChestBlockModel doubleChestLeft;
    @Shadow @Mutable private ChestBlockModel doubleChestRight;

    @Unique private ChestBlockModel BBEsingleChest;
    @Unique private ChestBlockModel BBEdoubleChestLeft;
    @Unique private ChestBlockModel BBEdoubleChestRight;

    @Unique private ChestBlockModel singleChestOrg;
    @Unique private ChestBlockModel doubleChestLeftOrg;
    @Unique private ChestBlockModel doubleChestRightOrg;

    /* replace the original built models with our own that removes the trunk */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void cacheAndInitModels(BlockEntityRendererFactory.Context context, CallbackInfo ci) {
        this.singleChestOrg = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
        this.doubleChestLeftOrg = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
        this.doubleChestRightOrg = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));

        this.BBEsingleChest = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
        this.BBEdoubleChestLeft = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
        this.BBEdoubleChestRight = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));
    }

    @Inject(method = "render(Lnet/minecraft/client/render/block/entity/state/ChestBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"))
    public void render(ChestBlockEntityRenderState chestBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (!ConfigManager.CONFIG.optimize_chests || !ConfigManager.CONFIG.master_optimize) {
            this.singleChest = singleChestOrg;
            this.doubleChestLeft = doubleChestLeftOrg;
            this.doubleChestRight = this.doubleChestRightOrg;
        }
        else {
            this.singleChest = this.BBEsingleChest;
            this.doubleChestLeft = this.BBEdoubleChestLeft;
            this.doubleChestRight = this.BBEdoubleChestRight;
        }
    }
}
