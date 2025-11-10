package betterblockentities.util;


/* minecraft */
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

/* java/misc */
import java.util.ArrayList;
import java.util.List;

public class BlockVisibilityChecker {
    /* checks if passed blockEntity is in the view frustum and LOS in not blocked */
    public static boolean isBlockInFOVAndVisible(Frustum frustum, BlockEntity blockEntity) {
        Entity player = MinecraftClient.getInstance().getCameraEntity();
        BlockPos blockPos = blockEntity.getPos();
        Vec3d eyePos = player.getCameraPosVec(1.0f);

        /* center of the block */
        Vec3d center = new Vec3d(
                blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5
        );

        /* max distance check */
        double maxDistance = 20.0;
        if (eyePos.squaredDistanceTo(center) > maxDistance * maxDistance) return false;

        /* check if block is inside view frustum */
        if (!isBlockInViewFrustum(frustum, blockEntity)) return false;

        /* LOS check to multiple points on the block */
        for (Vec3d offset : BLOCK_SAMPLE_OFFSETS) {
            Vec3d target = new Vec3d(
                    blockPos.getX() + offset.x,
                    blockPos.getY() + offset.y,
                    blockPos.getZ() + offset.z
            );

            HitResult hit = player.getWorld().raycast(new RaycastContext(
                    eyePos,
                    target,
                    RaycastContext.ShapeType.VISUAL,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));

            /* did we hit? return early */
            if (hit.getType() == HitResult.Type.BLOCK && ((BlockHitResult) hit).getBlockPos().equals(blockPos)) {
                return true;
            }
        }
        return false; //all sample points blocked
    }

    /* setup bounding box */
    private static Box setupBox(BlockEntity entity, BlockPos pos) {
        if (entity instanceof BannerBlockEntity)
            return new Box(pos).expand(0, 1, 0);
        return new Box(pos);
    }

    /* preform frustum visibility check */
    private static boolean isBlockInViewFrustum(Frustum frustum, BlockEntity blockEntity) {
        return frustum != null && frustum.isVisible(setupBox(blockEntity, blockEntity.getPos()));
    }

    /* generate sample points to raycast to */
    private static Vec3d[] generateFaceGrid(int resolution) {
        List<Vec3d> list = new ArrayList<>();
        double step = 1.0 / (resolution - 1);

        for (int y = 0; y < resolution; y++) {
            for (int x = 0; x < resolution; x++) {
                double u = x * step; // horizontal
                double v = y * step; // vertical

                list.add(new Vec3d(0.0, v, u));
                list.add(new Vec3d(1.0, v, u));
                list.add(new Vec3d(u, v, 0.0));
                list.add(new Vec3d(u, v, 1.0));
                list.add(new Vec3d(u, 0.0, v));
                list.add(new Vec3d(u, 1.0, v));
            }
        }
        return list.toArray(new Vec3d[0]);
    }

    /* sample points */
    private static final Vec3d[] BLOCK_SAMPLE_OFFSETS = generateFaceGrid(5);
}
