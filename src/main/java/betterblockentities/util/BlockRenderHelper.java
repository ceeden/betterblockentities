package betterblockentities.util;

/* fabric */
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

/* minecraft */
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.util.math.Direction;

/* java/misc */
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class BlockRenderHelper
{
    /* rebuild, takes List<BlockModelPart> instead of the whole model */
    public static void emitQuads(List<BlockModelPart> parts, QuadEmitter emitter, Predicate<@Nullable Direction> cullTest) {
        final int partCount = parts.size();
        for (int i = 0; i < partCount; i++) {
            parts.get(i).emitQuads(emitter, cullTest);
        }
    }

    /* compute rotation angle in degrees */
    public static float computeSignRotation(BlockState state) {
        if (state.contains(SignBlock.ROTATION)) {
            int rot = state.get(SignBlock.ROTATION);
            return rot * 22.5f;
        }
        return 0f;
    }
}
