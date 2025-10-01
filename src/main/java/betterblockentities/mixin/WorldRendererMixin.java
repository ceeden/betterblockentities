package betterblockentities.mixin;

/* local */
import betterblockentities.gui.ConfigManager;
import betterblockentities.helpers.BlockEntityManager;
import betterblockentities.helpers.BlockEntityTracker;

/* sodium */
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkUpdateType;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion;
import net.caffeinemc.mods.sodium.client.util.iterator.ByteIterator;

/* minecraft */
import net.minecraft.block.entity.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.BlockBreakingInfo;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

/* java/misc */
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Iterator;
import java.util.SortedSet;


@Mixin(SodiumWorldRenderer.class)
public abstract class WorldRendererMixin
{
    @Shadow
    private RenderSectionManager renderSectionManager;

    @Shadow
    protected static void renderBlockEntity(MatrixStack matrices, BufferBuilderStorage bufferBuilders, Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions, float tickDelta, VertexConsumerProvider.Immediate immediate, double x, double y, double z, BlockEntityRenderDispatcher dispatcher, BlockEntity entity, ClientPlayerEntity player, LocalBooleanRef isGlowing)
    { }

     /**
          @author ceeden
          @reason

          this adds upon the original sodium code and adds
          our own animation/chunk rebuild/update logic.

          we could definitely improve this code lol
          performance wise its alright might want to
          clean it up and put parts in separate
          helper classes
      */
    @Overwrite
    private void renderBlockEntities(MatrixStack matrices, BufferBuilderStorage bufferBuilders, Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions, float tickDelta, VertexConsumerProvider.Immediate immediate, double x, double y, double z, BlockEntityRenderDispatcher blockEntityRenderer, ClientPlayerEntity player, LocalBooleanRef isGlowing)
    {
        if (!ConfigManager.CONFIG.use_animations)
            return;

        SortedRenderLists renderLists = this.renderSectionManager.getRenderLists();
        for (Iterator<ChunkRenderList> it = renderLists.iterator(); it.hasNext(); )
        {
            ChunkRenderList renderList = it.next();
            RenderRegion renderRegion = renderList.getRegion();
            ByteIterator renderSectionIterator = renderList.sectionsWithEntitiesIterator();

            if (renderSectionIterator == null)
                return;

            while (renderSectionIterator.hasNext())
            {
                int renderSectionId = renderSectionIterator.nextByteAsInt();
                RenderSection renderSection = renderRegion.getSection(renderSectionId);
                BlockEntity[] blockEntities = renderSection.getCulledBlockEntities();

                if (blockEntities == null)
                    return;

                for (BlockEntity blockEntity : blockEntities)
                {
                    BlockEntityManager manager = new BlockEntityManager(blockEntity, renderSection, tickDelta);
                    if (manager.shouldRender())
                        this.renderBlockEntity(matrices, bufferBuilders, blockBreakingProgressions, tickDelta, immediate, x, y, z, blockEntityRenderer, blockEntity, player, isGlowing);
                    manager = null;
                }
            }
            if (BlockEntityTracker.sectionsToUpdate.isEmpty())
                return;

            for (RenderSection section : BlockEntityTracker.sectionsToUpdate) {
                section.setPendingUpdate(ChunkUpdateType.IMPORTANT_REBUILD);
            }
            BlockEntityTracker.sectionsToUpdate.clear();
            this.renderSectionManager.updateChunks(true);
            this.renderSectionManager.markGraphDirty();
        }
    }
}


