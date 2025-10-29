package betterblockentities.mixin;

/* local */
import betterblockentities.gui.ConfigManager;
import betterblockentities.model.BBEChestBlockModel;

/* minecraft */
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;

/* mixin */
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestBlockEntityRendererMixin<T extends BlockEntity & LidOpenable> {

    @Shadow @Mutable private ChestBlockModel singleChest;
    @Shadow @Mutable private ChestBlockModel doubleChestLeft;
    @Shadow @Mutable private ChestBlockModel doubleChestRight;

    @Unique private ChestBlockModel BBEsingleChest;
    @Unique private ChestBlockModel BBEdoubleChestLeft;
    @Unique private ChestBlockModel BBEdoubleChestRight;

    @Unique private ChestBlockModel singleChestOrg;
    @Unique private ChestBlockModel doubleChestLeftOrg;
    @Unique private ChestBlockModel doubleChestRightOrg;

    // Initialize custom models
    @Inject(method = "<init>", at = @At("RETURN"))
    private void cacheAndInitModels(BlockEntityRendererFactory.Context context, CallbackInfo ci) {
        this.singleChestOrg = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
        this.doubleChestLeftOrg = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
        this.doubleChestRightOrg = new ChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));

        this.BBEsingleChest = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
        this.BBEdoubleChestLeft = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
        this.BBEdoubleChestRight = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));
    }

    // Swap models before rendering
    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"))
    private void render(T entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos, CallbackInfo ci) {
        if (!ConfigManager.CONFIG.optimize_chests || !ConfigManager.CONFIG.master_optimize) {
            this.singleChest = singleChestOrg;
            this.doubleChestLeft = doubleChestLeftOrg;
            this.doubleChestRight = doubleChestRightOrg;
        } else {
            this.singleChest = BBEsingleChest;
            this.doubleChestLeft = BBEdoubleChestLeft;
            this.doubleChestRight = BBEdoubleChestRight;
        }
    }
}
