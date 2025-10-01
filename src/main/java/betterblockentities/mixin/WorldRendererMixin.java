package betterblockentities.mixin;

/* local */
import betterblockentities.gui.ConfigManager;
import betterblockentities.helpers.BlockEntityManager;
import betterblockentities.helpers.BlockEntityTracker;

/* sodium */
import betterblockentities.helpers.ChunkUpdateManager;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkUpdateTypes;
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
import net.minecraft.client.render.state.WorldRenderState;
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
    private void extractBlockEntity(BlockEntity blockEntity, MatrixStack poseStack, Camera camera, float tickDelta, Long2ObjectMap<SortedSet<BlockBreakingInfo>> progression, WorldRenderState levelRenderState)
    {
    }

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
    public void extractBlockEntities(Camera camera, float tickDelta, Long2ObjectMap<SortedSet<BlockBreakingInfo>> progression, WorldRenderState levelRenderState)
    {
        if (!ConfigManager.CONFIG.use_animations)
            return;

        MatrixStack stack = new MatrixStack();

        SortedRenderLists renderLists = this.renderSectionManager.getRenderLists();
        for (Iterator<ChunkRenderList> it = renderLists.iterator(); it.hasNext();)
        {
            ChunkRenderList renderList = it.next();

            RenderRegion renderRegion = renderList.getRegion();
            ByteIterator renderSectionIterator = renderList.sectionsWithEntitiesIterator();

            if (renderSectionIterator != null)
            {
                while (renderSectionIterator.hasNext())
                {
                    int renderSectionId = renderSectionIterator.nextByteAsInt();
                    RenderSection renderSection = renderRegion.getSection(renderSectionId);
                    BlockEntity[] blockEntities = renderSection.getCulledBlockEntities();

                    if (blockEntities != null)
                    {
                        for (BlockEntity blockEntity : blockEntities)
                        {
                            BlockEntityManager manager = new BlockEntityManager(blockEntity, renderSection, tickDelta);
                            if (manager.shouldRender())
                                this.extractBlockEntity(blockEntity, stack, camera, tickDelta, progression, levelRenderState);
                            manager = null;
                        }
                    }
                }

                if (!BlockEntityTracker.sectionsToUpdate.isEmpty())
                {
                    for (RenderSection section : BlockEntityTracker.sectionsToUpdate) {
                        section.setPendingUpdate(ChunkUpdateTypes.REBUILD, 0);
                    }
                    BlockEntityTracker.sectionsToUpdate.clear();
                    this.renderSectionManager.updateChunks(true);
                    this.renderSectionManager.markGraphDirty();
                }
            }
        }
    }
}