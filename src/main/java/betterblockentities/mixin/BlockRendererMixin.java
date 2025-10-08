package betterblockentities.mixin;

/* local */
import betterblockentities.helpers.BlockEntityManager;
import betterblockentities.helpers.BlockEntityTracker;

/* minecraft */
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.block.*;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.math.BlockPos;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin
{
    /* sodium flow: RenderSectionManager->createRebuildTask->new ChunkBuilderMeshingTask->renderModel */
    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    private void IgnoreAnimatingBlockEntity(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci)
    {
        if (BlockEntityManager.isSupportedBlock(state.getBlock())) {
            boolean anim = BlockEntityTracker.animMap.contains(pos);
            if (anim) ci.cancel();
        }
    }
}
