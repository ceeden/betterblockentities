package betterblockentities.chunk;

/* minecraft */
import betterblockentities.BetterBlockEntities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/*
    TODO: add more rebuild options, like rebuild then perform task...
*/

public class ChunkUpdateDispatcher {
    public static void queueRebuildAtBlockPos(World world, long pos) {
        try {
            BlockPos posObj = BlockPos.fromLong(pos);
            var state = world.getBlockState(posObj);
            MinecraftClient.getInstance().worldRenderer.updateBlock(world, posObj, state, state, 8);
        } catch (Exception e) {
            BetterBlockEntities.getLogger().error("Error: Failed to update render section at {}", pos, e);
        }
    }
}
