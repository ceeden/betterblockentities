package betterblockentities.util;

/* minecraft */
import net.minecraft.util.math.BlockPos;

/* java/misc */
import java.util.*;

public class BlockEntityTracker
{
    public static HashSet<BlockPos> animMap = new HashSet<>();
    public static HashMap<BlockPos, Integer> extraRenderPasses = new HashMap<>();
}
