package betterblockentities.mixin;

/* local */
import betterblockentities.helpers.BlockEntityManager;
import betterblockentities.helpers.BlockEntityTracker;
import betterblockentities.helpers.ModelRotationTransform;

/* sodium */
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;

/* fabric */
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;

/* minecraft */
import net.minecraft.block.*;
import net.minecraft.client.font.*;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/* mixin */
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* java/misc */
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
    @Shadow @Final private Vector3f posOffset;
    @Shadow private @Nullable ColorProvider<BlockState> colorProvider;
    @Shadow @Final private ColorProviderRegistry colorProviderRegistry;

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    private void renderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        Block block = state.getBlock();

        /* check if the current block is supported and animating, cancel this entry to the mesh */
        if (BlockEntityManager.isSupportedBlock(block) && BlockEntityTracker.animMap.contains(pos)) {
            ci.cancel();
            return;
        }

        /*
            static logic for signs, only hanged(ceiling) and standing sign because
            they use different rotation logics, rotation for wall/hanging-wall signs is handled in
            the blockstate json files
        */
        if (block instanceof SignBlock || block instanceof HangingSignBlock) {
            ci.cancel();

            /* get AbstractBlockRenderContext access through our interface accessor */
            AbstractBlockRenderContextAccessor acc = (AbstractBlockRenderContextAccessor)(Object)this;
            final QuadEmitter emitter = acc.getEmitterInvoke();

            /* setup context */
            acc.setState(state);
            acc.setPos(pos);
            acc.prepareAoInfoInvoke(true);

            this.posOffset.set((float) origin.getX(), (float) origin.getY(), (float) origin.getZ());
            if (state.hasModelOffset()) {
                Vec3d offset = state.getModelOffset(pos);
                this.posOffset.add((float) offset.x, (float) offset.y, (float) offset.z);
            }

            this.colorProvider = this.colorProviderRegistry.getColorProvider(state.getBlock());
            acc.prepareCullingInvoke(true);
            acc.setDefaultRenderType(RenderLayers.getBlockLayer(state));
            acc.setAllowDowngrade(true);
            acc.getRandom().setSeed(state.getRenderingSeed(pos));

            /* compute rotation angle from sign state */
            float angle = 0f;
            if (state.contains(SignBlock.ROTATION)) {
                int rot = state.get(SignBlock.ROTATION);
                angle = rot * 22.5f;
            }

            /* pushTransform (rotate around y-axis) */
            emitter.pushTransform(ModelRotationTransform.rotateY(angle));

            /* emit quads */
            ((FabricBlockStateModel)model).emitQuads(emitter, acc.getLevel(), pos, state, acc.getRandom(), acc::isFaceCulledInvoke);

            /* popTransform (remove the transformer), restore context */
            emitter.popTransform();
            acc.setDefaultRenderType(null);
        }
    }
}
