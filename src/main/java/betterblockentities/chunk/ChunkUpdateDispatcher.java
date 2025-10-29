package betterblockentities.chunk;

/* minecraft */
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/*
    TODO: add more rebuild options, like rebuild then perform task...
*/

public class ChunkUpdateDispatcher {
    public static void queueRebuildAtBlockPos(World world, BlockPos pos) {
        var state = world.getBlockState(pos);
        MinecraftClient.getInstance().worldRenderer.updateBlock(world, pos, state, state, 8);
    }
}
