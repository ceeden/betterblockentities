package betterblockentities.mixin.sodium;

/* local */
import betterblockentities.ModelLoader;
import betterblockentities.gui.ConfigManager;
import betterblockentities.util.*;

/* sodium */
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;

/* fabric */
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;

/* minecraft */
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

/* mixin */
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* java/misc */
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Pseudo
@Mixin(BlockRenderer.class)
public class BlockRendererMixin {
    @Shadow @Final private Vector3f posOffset;
    @Shadow @Nullable private ColorProvider<BlockState> colorProvider;
    @Shadow @Final private ColorProviderRegistry colorProviderRegistry;

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    private void renderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        Block block = state.getBlock();
        ClientWorld world = MinecraftClient.getInstance().world;

        if (world == null)
            return;

        BlockEntity blockEntity = world.getBlockEntity(pos);

        /* safe cast BlockEntityExt to prevent crashes if instance is null */
        BlockEntityExt ext = (blockEntity instanceof BlockEntityExt bex) ? bex : null;
        boolean shouldRender = shouldRender(ext);

        if (BlockEntityManager.isSupportedBlock(block) && !ConfigManager.CONFIG.master_optimize) {
            if (block instanceof BellBlock) return;
            if (block instanceof BedBlock) return;

            ci.cancel();
            return;
        }

        /* setup context */
        AbstractBlockRenderContextAccessor acc = setupContext(state, pos, origin);
        final QuadEmitter emitter = acc.getEmitterInvoke();

        /* SIGNS */
        if (block instanceof SignBlock || block instanceof HangingSignBlock) {
            ci.cancel();

            if (!ConfigManager.CONFIG.optimize_signs) return;

            emitter.pushTransform(ModelTransform.rotateY(BlockRenderHelper.computeSignRotation(state)));
            ((FabricBlockStateModel) model).emitQuads(emitter, acc.getLevel(), pos, state, acc.getRandom(), acc::isFaceCulledInvoke);
            emitter.popTransform();
        }
        else if (block instanceof WallHangingSignBlock || block instanceof WallSignBlock) {
            if (!ConfigManager.CONFIG.optimize_signs)
                ci.cancel();
        }

        /* SHULKERS, and CHESTS */
        else if (block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof ShulkerBoxBlock) {
            boolean isShulker = block instanceof ShulkerBoxBlock;

            if ((isShulker && !ConfigManager.CONFIG.optimize_shulkers) ||
                    (!isShulker && !ConfigManager.CONFIG.optimize_chests))
                return;

            ci.cancel();

            List<BlockModelPart> parts = model.getParts(acc.getRandom());

            int quadThreshold = isShulker ? 10 : 6;

            Map<Boolean, List<BlockModelPart>> partitioned = parts.stream()
                    .collect(Collectors.partitioningBy(p -> p.getQuads(null).size() > quadThreshold));

            List<BlockModelPart> lidParts   = partitioned.get(true);
            List<BlockModelPart> trunkParts = partitioned.get(false);

            List<BlockModelPart> merged = new ArrayList<>(trunkParts);
            if (shouldRender) {
                merged.addAll(lidParts);
            }

            BlockRenderHelper.emitQuads(merged, emitter, acc::isFaceCulledInvoke);
        }

        /* BELLS */
        else if (block instanceof BellBlock) {
            if (!ConfigManager.CONFIG.optimize_bells) return;
            ci.cancel();

            Random rand = acc.getRandom();

            List<BlockModelPart> bell_part = model.getParts(rand);

            List<BlockModelPart> bell_body_part = new ArrayList<>();

            if (shouldRender) {
                BakedModelManager manager = MinecraftClient.getInstance().getBakedModelManager();
                BlockStateModel bell_body = manager.getModel(ModelLoader.BELL_BODY_KEY);
                bell_body_part.addAll(bell_body.getParts(rand));
            }

            List<BlockModelPart> merged = new ArrayList<>(bell_part);
            if (!bell_body_part.isEmpty())
                merged.addAll(bell_body_part);

            BlockRenderHelper.emitQuads(merged, emitter, acc::isFaceCulledInvoke);
        }

        /* DECORATED POTS */
        else if (block instanceof DecoratedPotBlock) {
            if (!ConfigManager.CONFIG.optimize_decoratedpots) {
                ci.cancel();
                return;
            }
            if (!shouldRender)
                ci.cancel();
        }
        restoreContext();
    }

    @Unique
    private boolean shouldRender(BlockEntityExt ext) {
        return ext == null || !ext.getRemoveChunkVariant();
    }

    @Unique
    AbstractBlockRenderContextAccessor setupContext(BlockState state, BlockPos pos, BlockPos origin) {
        AbstractBlockRenderContextAccessor acc = (AbstractBlockRenderContextAccessor)(Object)this;
        acc.setState(state);
        acc.setPos(pos);
        acc.prepareAoInfoInvoke(true);

        this.posOffset.set(origin.getX(), origin.getY(), origin.getZ());
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
        AbstractBlockRenderContextAccessor acc =
                (AbstractBlockRenderContextAccessor)(Object)this;
        acc.setDefaultRenderType(null);
    }
}