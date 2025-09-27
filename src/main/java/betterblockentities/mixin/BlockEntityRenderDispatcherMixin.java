package betterblockentities.mixin;

/* minecraft */
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin
{
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void DispatchCustomRenderers(E blockEntity, CallbackInfoReturnable<BlockEntityRenderer<E>> cir)
    {
        /*
            you could deploy your own renderers here, if you wanted
            custom models etc... remember this only executes when
            we are animating so make sure to change the Model JSON
            for the chunk mesh (minecraft/models/block/chest.json)
            for example

            example snippet for custom models:
                   for (BakedQuad quad : CUSTOMQUADS) {
                        consumer.quad(matrices.peek(), CUSTOMQUADS, 1f, 1f, 1f, 1f, light, overlay);
                   }
            in your custom BlockEntityRenderer, CUSTOMQUADS would
            come from your CUSTOM_MODEL(BlockStateModel)->getParts()->getQuads()
        */
    }
}
