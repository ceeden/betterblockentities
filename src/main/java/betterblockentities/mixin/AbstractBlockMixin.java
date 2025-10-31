package betterblockentities.mixin;

/* local */
import betterblockentities.util.BlockEntityManager;

/* minecraft */
import net.minecraft.block.*;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin
{
    /* apparently we do not need this? getRenderType always return type MODEL anyway... */
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void forceToMesh(BlockState state, CallbackInfoReturnable<BlockRenderType> cir) {
        if (BlockEntityManager.isSupportedBlock(state.getBlock()))
            cir.setReturnValue(BlockRenderType.MODEL);
    }
}
