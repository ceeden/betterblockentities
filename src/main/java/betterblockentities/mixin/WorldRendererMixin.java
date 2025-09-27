package betterblockentities.mixin;

/* local */
import betterblockentities.gui.ConfigManager;
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

        /* add all blocks with ongoing breaking animation. do we need this????
        for (Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>> entry : blockBreakingProgressions.long2ObjectEntrySet())
        {
            SortedSet<BlockBreakingInfo> set = entry.getValue();
            for (BlockBreakingInfo info : set) {
                BlockEntityTracker.blockBreakingMap.add(info.getPos());
            }
        }
         */

        SortedRenderLists renderLists = this.renderSectionManager.getRenderLists();
        for (Iterator<ChunkRenderList> it = renderLists.iterator(); it.hasNext(); )
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
                            if (/*!BlockEntityTracker.blockBreakingMap.contains(blockEntity.getPos()) &&*/
                                    blockEntity instanceof ChestBlockEntity || blockEntity instanceof EnderChestBlockEntity
                                    || blockEntity instanceof ShulkerBoxBlockEntity || blockEntity instanceof TrappedChestBlockEntity)
                            {
                                float animProg;
                                if (blockEntity instanceof ShulkerBoxBlockEntity)
                                    animProg = ((ShulkerBoxBlockEntity) blockEntity).getAnimationProgress(tickDelta);
                                else {
                                    LidOpenable lid = (LidOpenable)(blockEntity);
                                    animProg = lid.getAnimationProgress(tickDelta);
                                }

                                /*
                                    if we are animating, render block entity with block entity renderer.
                                    add to animating map, add to chunk update map

                                    if not animating, remove from animating map, add to chunk update map
                                */
                                if (animProg > 0.00)
                                {
                                    if (!(BlockEntityTracker.animMap.contains(blockEntity.getPos())))
                                    {
                                        BlockEntityTracker.animMap.add(blockEntity.getPos());
                                        BlockEntityTracker.sectionsToUpdate.add(renderSection);
                                    }
                                    this.renderBlockEntity(matrices, bufferBuilders, blockBreakingProgressions, tickDelta, immediate, x, y, z, blockEntityRenderer, blockEntity, player, isGlowing);
                                }
                                else
                                {
                                    if (BlockEntityTracker.animMap.contains(blockEntity.getPos()))
                                    {
                                        BlockEntityTracker.animMap.remove(blockEntity.getPos());
                                        BlockEntityTracker.sectionsToUpdate.add(renderSection);
                                        BlockEntityTracker.extraRenderPasses.put(blockEntity.getPos(), ConfigManager.CONFIG.smoothness_slider);
                                    }
                                }

                                /*
                                    keep rendering the chest with BlockEntityRenderer for 3 passes after
                                    it is done animating to get rid of the harsh transition between
                                    "entity rendered chest" and "mesh chest"

                                    found 3 to work the best, you could modify the passes depending on the
                                    transition smoothness you want (miniscule performance impact)
                                */
                                if (BlockEntityTracker.extraRenderPasses.containsKey(blockEntity.getPos()))
                                {
                                    int passes = BlockEntityTracker.extraRenderPasses.get(blockEntity.getPos());
                                    if (passes > 0)
                                    {
                                        BlockEntityTracker.extraRenderPasses.put(blockEntity.getPos(), passes - 1);
                                        this.renderBlockEntity(matrices, bufferBuilders, blockBreakingProgressions, tickDelta, immediate, x, y, z, blockEntityRenderer, blockEntity, player, isGlowing);
                                    }
                                    else {
                                        BlockEntityTracker.extraRenderPasses.remove(blockEntity.getPos());
                                    }
                                }
                            }
                            else
                            {
                                /*
                                    render other BlockEntities normally for now (signs, beacons, etc...)
                                    OBS: the code above won't work for other block entities like beacons
                                    as they animate all the time (they don't have a lid)
                                */
                                /*BlockEntityTracker.blockBreakingMap.remove(blockEntity.getPos());*/
                                this.renderBlockEntity(matrices, bufferBuilders, blockBreakingProgressions, tickDelta, immediate, x, y, z, blockEntityRenderer, blockEntity, player, isGlowing);
                            }
                        }
                    }
                }
                /*
                    "chunk rebuild/update flow":
                     -setPendingUpdate() - flags the section for rebuilding
                     -updateChunks() - rebuild flagged sections immediately
                     -markGraphDirty() - ensure visibility is recalculated
                */
                if (!BlockEntityTracker.sectionsToUpdate.isEmpty())
                {
                    for (RenderSection section : BlockEntityTracker.sectionsToUpdate) {
                        section.setPendingUpdate(ChunkUpdateType.IMPORTANT_REBUILD);
                    }
                    BlockEntityTracker.sectionsToUpdate.clear();
                    this.renderSectionManager.updateChunks(true);
                    this.renderSectionManager.markGraphDirty();
                }
            }
        }
    }
}