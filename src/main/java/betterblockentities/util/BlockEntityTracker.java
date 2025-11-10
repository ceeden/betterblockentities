package betterblockentities.util;

/* java/misc */
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class BlockEntityTracker {
    public static final LongSet animMap = new LongOpenHashSet();
    public static final Long2IntMap extraRenderPasses = new Long2IntOpenHashMap();
}
