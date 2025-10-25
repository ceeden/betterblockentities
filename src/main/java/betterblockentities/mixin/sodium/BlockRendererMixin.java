package betterblockentities.mixin.sodium;

/* local */
import betterblockentities.ModelLoader;
import betterblockentities.gui.ConfigManager;
import betterblockentities.helpers.BlockEntityManager;
import betterblockentities.helpers.BlockEntityTracker;
import betterblockentities.helpers.BlockRenderHelper;
import betterblockentities.helpers.ModelTransform;

/* sodium */
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;

/* fabric */
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;

/* minecraft */
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.model.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/* mixin */
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* java/misc */
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {
    @Shadow @Final private Vector3f posOffset;
    @Shadow @Nullable private ColorProvider<BlockState> colorProvider;
    @Shadow @Final private ColorProviderRegistry colorProviderRegistry;

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    private void renderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        Block block = state.getBlock();

        /* is the block supported? */
        if (BlockEntityManager.isSupportedBlock(block) && !ConfigManager.CONFIG.master_optimize) {
            /* exclude bell because it has parts in the mesh by default */
            if (block instanceof BellBlock) return;
            ci.cancel(); return;
        }

        /* lazy load our models, should probably do this elsewhere */
        ModelLoader.loadModels();

        /* set up the mesh context */
        AbstractBlockRenderContextAccessor acc = setupContext(state, pos, origin);
        final QuadEmitter emitter = acc.getEmitterInvoke();

        /* handle these signs differently as they have a blockstate rotation property, rotate them with our custom transform */
        if (block instanceof SignBlock || block instanceof HangingSignBlock) {
            ci.cancel(); if (!ConfigManager.CONFIG.optimize_signs) return;

            emitter.pushTransform(ModelTransform.rotateY(BlockRenderHelper.computeSignRotation(state)));
            ((FabricBlockStateModel)model).emitQuads(emitter, acc.getLevel(), pos, state, acc.getRandom(), acc::isFaceCulledInvoke);
            emitter.popTransform();
        }

        /* nothing special here */
        else if (block instanceof WallHangingSignBlock || block instanceof WallSignBlock) {
            if (!ConfigManager.CONFIG.optimize_signs) ci.cancel();
        }

        /*
            split trunk/base and lid quads from the MultiPartBlockStateModel, no transform needed as this is handled in our generated
            blockstate jsons
        */
        else if (block instanceof ChestBlock || block instanceof EnderChestBlock) {
            ci.cancel(); if (!ConfigManager.CONFIG.optimize_chests) return;

            List<BlockModelPart> parts = model.getParts(acc.getRandom());

            /*
                super pseudo, this is straight ass, couldn't come up with a better way to do it sooo...
                TODO: find a better way to identify which part is lid and which part is base/trunk!
            */
            int quadThreshold = (block instanceof ShulkerBoxBlock) ? 10 : 6;
            Map<Boolean, List<BlockModelPart>> partitioned = parts.stream()
                    .collect(Collectors.partitioningBy(p -> p.getQuads(null).size() > quadThreshold));

            List<BlockModelPart> lidParts = partitioned.get(true);
            List<BlockModelPart> trunkParts = partitioned.get(false);

            if (!BlockEntityTracker.animMap.contains(pos))
                BlockRenderHelper.emitQuads(lidParts, emitter, acc::isFaceCulledInvoke);
            BlockRenderHelper.emitQuads(trunkParts, emitter, acc::isFaceCulledInvoke);
        }

        else if (block instanceof ShulkerBoxBlock) {
            ci.cancel(); if (!ConfigManager.CONFIG.optimize_shulkers) return;

            List<BlockModelPart> parts = model.getParts(acc.getRandom());

            /*
                super pseudo, this is straight ass, couldn't come up with a better way to do it sooo...
                TODO: find a better way to identify which part is lid and which part is base/trunk!
            */
            int quadThreshold = (block instanceof ShulkerBoxBlock) ? 10 : 6;
            Map<Boolean, List<BlockModelPart>> partitioned = parts.stream()
                    .collect(Collectors.partitioningBy(p -> p.getQuads(null).size() > quadThreshold));

            List<BlockModelPart> lidParts = partitioned.get(true);
            List<BlockModelPart> trunkParts = partitioned.get(false);

            if (!BlockEntityTracker.animMap.contains(pos))
                BlockRenderHelper.emitQuads(lidParts, emitter, acc::isFaceCulledInvoke);
            BlockRenderHelper.emitQuads(trunkParts, emitter, acc::isFaceCulledInvoke);
        }

        /*
            nothing special for bells, because it already emits/renders the bars, post, etc. in the mesh
            so we just add in the bell body(we could probably retrieve this from else where than our
            custom model loader probably) and remove it when we are animating. much less code doing
            it like this instead of copying the chest setup and using a MultiPartBlockStateModel,
            generating models, blockstate jsons, etc...
        */
        else if (block instanceof BellBlock) {
            if (!ConfigManager.CONFIG.optimize_bells) return; ci.cancel();

            if (!BlockEntityTracker.animMap.contains(pos))
                ((FabricBlockStateModel)ModelLoader.bell_body).emitQuads(emitter, acc.getLevel(), pos, state, acc.getRandom(), acc::isFaceCulledInvoke);
            ((FabricBlockStateModel)model).emitQuads(emitter, acc.getLevel(), pos, state, acc.getRandom(), acc::isFaceCulledInvoke);
        }

        else if (block instanceof DecoratedPotBlock) {
            if (!ConfigManager.CONFIG.optimize_decoratedpots) {
                ci.cancel(); return;
            }
            if (BlockEntityTracker.animMap.contains(pos)) ci.cancel();
        }

        else if (block instanceof BedBlock) {
            //if (!ConfigManager.CONFIG.optimize_beds)
                //ci.cancel();
        }

        /* don't emit to the mesh if the block is supported and animating but not caught above */
        else if (BlockEntityTracker.animMap.contains(pos)) ci.cancel();

        restoreContext();
    }

    /* setup the "mesh" context, straight copy/paste from the original renderModel function */
    @Unique
    AbstractBlockRenderContextAccessor setupContext(BlockState state, BlockPos pos, BlockPos origin) {
        AbstractBlockRenderContextAccessor acc = (AbstractBlockRenderContextAccessor)(Object)this;
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
        return acc;
    }

    @Unique
    void restoreContext() {
        AbstractBlockRenderContextAccessor acc = (AbstractBlockRenderContextAccessor)(Object)this;
        acc.setDefaultRenderType(null);
    }
}
