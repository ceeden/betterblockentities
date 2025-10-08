package betterblockentities.mixin;

/* minecraft */
import betterblockentities.helpers.BlockEntityManager;
import net.minecraft.block.*;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin
{
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void ForceToMesh(BlockState state, CallbackInfoReturnable<BlockRenderType> cir) {
        if (BlockEntityManager.isSupportedBlock(state.getBlock()))
            cir.setReturnValue(BlockRenderType.MODEL);
    }
}
