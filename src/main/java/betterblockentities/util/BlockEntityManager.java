package betterblockentities.util;

/* local */
import betterblockentities.chunk.ChunkUpdateDispatcher;
import betterblockentities.gui.ConfigManager;

/* minecraft */
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/* java/misc */
import java.util.Collections;
import java.util.Set;

/*
    TODO: instead of sending chunk updates for each iteration,
          capture block pos of all blocks that needs a update
          and just send all the updates after collection.
 */

public class BlockEntityManager {
    public static Set<Class<? extends BlockEntity>> SUPPORTED_TYPES = Collections.emptySet();
    public static final Set<Class<? extends Block>> SUPPORTED_BLOCKS = Set.of(
            ChestBlock.class, EnderChestBlock.class, ShulkerBoxBlock.class,
            BellBlock.class, DecoratedPotBlock.class, BedBlock.class,
            SignBlock.class, HangingSignBlock.class,
            WallSignBlock.class, WallHangingSignBlock.class
    );

    public static boolean chestAnims, shulkerAnims, bellAnims, potAnims, signText, masterOptimize;
    public static int smoothness;

    public static boolean isSupportedBlock(Block block) {
        return block != null && SUPPORTED_BLOCKS.contains(block.getClass());
    }

    public static boolean isSupportedEntity(BlockEntity blockEntity) {
        return blockEntity != null && SUPPORTED_TYPES.contains(blockEntity.getClass());
    }

    public static boolean isSignEntity(BlockEntity blockEntity) {
        return blockEntity != null &&
                SignBlockEntity.class == blockEntity.getClass() ||
                HangingSignBlockEntity.class == blockEntity.getClass();
    }

    private static boolean isAnimating(BlockEntity blockEntity) {
        if (chestAnims && blockEntity instanceof LidOpenable lid)
            return lid.getAnimationProgress(0.5f) > 0f;
        if (shulkerAnims && blockEntity instanceof ShulkerBoxBlockEntity shulker)
            return shulker.getAnimationProgress(0.5f) > 0f;
        if (bellAnims && blockEntity instanceof BellBlockEntity bell)
            return bell.ringing;
        if (potAnims && blockEntity instanceof DecoratedPotBlockEntity pot && pot.lastWobbleType != null) {
            long now = blockEntity.getWorld().getTime();
            return now - pot.lastWobbleTime < pot.lastWobbleType.lengthInTicks;
        }
        if (signText && blockEntity instanceof SignBlockEntity) {
            //int chunkRenderDistance = MinecraftClient.getInstance().options.getViewDistance().getValue();

            /* distance in blocks not chunks */
            double maxSignTextDistance = ConfigManager.CONFIG.sign_text_render_distance;

            Entity entity = MinecraftClient.getInstance().getCameraEntity();
            boolean shouldRenderText = entity.squaredDistanceTo(Vec3d.ofCenter(blockEntity.getPos())) < maxSignTextDistance * maxSignTextDistance;
            if (shouldRenderText)
                return true;
        }
        return false;
    }

    public static boolean shouldRender(BlockEntity blockEntity) {
        if (!masterOptimize) return true;

        /* are we a supported BE and are the config options enabled */
        if (!isSupportedEntity(blockEntity)) return true;

        /* did we just receive a block event, if not don't render with BER */
        BlockEntityExt inst = (BlockEntityExt) blockEntity;
        if (!inst.getJustReceivedUpdate() && !isSignEntity(blockEntity)) {
            return false;
        }

        /* animation logic (static and animating) */
        return isAnimating(blockEntity) ? handleAnimating(blockEntity, inst) : handleStatic(blockEntity, inst);
    }

    private static boolean handleAnimating(BlockEntity blockEntity, BlockEntityExt inst) {
        var pos = blockEntity.getPos();

        /* ignore signs as we render the text with its BER  */
        if (!(blockEntity instanceof SignBlockEntity)) {
            /* add to anim map if an entry doesn't exist */
            if (BlockEntityTracker.animMap.add(pos)) {
                inst.setRemoveChunkVariant(true);
                ChunkUpdateDispatcher.queueRebuildAtBlockPos(blockEntity.getWorld(), pos);
            }
        }
        return true;
    }

    private static boolean handleStatic(BlockEntity blockEntity, BlockEntityExt inst) {
        var pos = blockEntity.getPos();

        /* remove from anim map if entry exists  */
        if (BlockEntityTracker.animMap.remove(pos)) {
            inst.setRemoveChunkVariant(false);
            ChunkUpdateDispatcher.queueRebuildAtBlockPos(blockEntity.getWorld(), pos);
            BlockEntityTracker.extraRenderPasses.put(pos, smoothness);
        }

        /* keep rendering for x amount of passes after BE stopped animating  */
        Integer passes = BlockEntityTracker.extraRenderPasses.compute(pos, (p, v) -> {
            if (v == null) return null;
            if (v > 1) return v - 1;
            inst.setJustReceivedUpdate(false);
            return null;
        });
        return passes != null;
    }
}
