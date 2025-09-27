package betterblockentities.mixin;

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
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void ForceToMesh(BlockState state, CallbackInfoReturnable<BlockRenderType> cir)
    {
        Block block = state.getBlock();
        if (block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof ShulkerBoxBlock || block instanceof TrappedChestBlock)
            cir.setReturnValue(BlockRenderType.MODEL);
    }
}
