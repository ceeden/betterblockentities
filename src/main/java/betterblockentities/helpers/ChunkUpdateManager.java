package betterblockentities.helpers;

import net.caffeinemc.mods.sodium.client.render.chunk.ChunkUpdateTypes;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;

public class ChunkUpdateManager
{
    private static RenderSectionManager manager;

    public ChunkUpdateManager(RenderSectionManager manager) {
        this.manager = manager;
    }

    public void updateTrackedSections()
    {
        if (!BlockEntityTracker.sectionsToUpdate.isEmpty())
        {
            for (RenderSection section : BlockEntityTracker.sectionsToUpdate) {
                section.setPendingUpdate(ChunkUpdateTypes.REBUILD, 0);
            }
            BlockEntityTracker.sectionsToUpdate.clear();
            manager.updateChunks(true);
            manager.markGraphDirty();
        }
    }
}
