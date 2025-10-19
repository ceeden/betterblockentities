package betterblockentities.mixin;

/* local */
import betterblockentities.model.BBEChestBlockModel;

/* minecraft */
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;

/* mixin */
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

    /* replace the original built models with our own that removes the trunk */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void replaceModels(BlockEntityRendererFactory.Context context, CallbackInfo ci) {
        this.singleChest = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.CHEST));
        this.doubleChestLeft = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT));
        this.doubleChestRight = new BBEChestBlockModel(context.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT));
    }
}
