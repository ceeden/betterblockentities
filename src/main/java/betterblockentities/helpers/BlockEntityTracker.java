package betterblockentities.helpers;

import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class BlockEntityTracker
{
    public static HashSet<BlockPos> animMap = new HashSet<>();
    public static HashSet<BlockPos> blockBreakingMap = new HashSet<>();
    public static HashSet<RenderSection> sectionsToUpdate = new HashSet<>();
    public static HashMap<BlockPos, Integer> extraRenderPasses = new HashMap<>();
}
