package betterblockentities.helpers;

/* sodium */
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;

/* minecraft */
import net.minecraft.util.math.BlockPos;

/* java/misc */
import java.util.*;

public class BlockEntityTracker
{
    public static HashSet<BlockPos> animMap = new HashSet<>();
    public static HashSet<RenderSection> sectionsToUpdate = new HashSet<>();
    public static HashMap<BlockPos, Integer> extraRenderPasses = new HashMap<>();
}
