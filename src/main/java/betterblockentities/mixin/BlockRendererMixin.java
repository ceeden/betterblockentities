package betterblockentities.mixin;

/* local */
import betterblockentities.helpers.BlockEntityTracker;

/* minecraft */
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

/* mixin */
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin
{
    /*
        this function gets called for every block when the chunk is
        building/rebuilding its mesh. skips rendering the block
        if it´s animating. we need to force a chunk update for this
        to take effect

        sodium flow:
        RenderSectionManager->createRebuildTask->new ChunkBuilderMeshingTask->renderModel
    */
    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    private void IgnoreAnimatingBlockEntity(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci)
    {
        boolean anim = BlockEntityTracker.animMap.contains(pos);
        if (anim) ci.cancel();
    }
}
