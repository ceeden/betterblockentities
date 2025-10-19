package betterblockentities.helpers;

/* local */
import betterblockentities.gui.ConfigManager;

/* sodium */
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;

/* minecraft */
import net.minecraft.block.*;
import net.minecraft.block.entity.*;

public class BlockEntityManager
{
    private final BlockEntity blockEntity;
    private final RenderSection renderSection;
    private final float tickDelta;

    public BlockEntityManager(BlockEntity blockEntity, RenderSection renderSection, float tickDelta) {
        this.blockEntity = blockEntity;
        this.renderSection = renderSection;
        this.tickDelta = tickDelta;
    }

    public static boolean isSupportedBlock(Block block) {
        return block instanceof ChestBlock
                || block instanceof EnderChestBlock
                || block instanceof ShulkerBoxBlock
                || block instanceof BellBlock
                || block instanceof DecoratedPotBlock
                || block instanceof BedBlock
                || block instanceof SignBlock
                || block instanceof HangingSignBlock
                || block instanceof WallSignBlock
                || block instanceof WallHangingSignBlock;
    }

    private boolean isSupportedEntity() {
        return blockEntity instanceof ChestBlockEntity
                || blockEntity instanceof EnderChestBlockEntity
                || blockEntity instanceof ShulkerBoxBlockEntity
                || blockEntity instanceof BellBlockEntity
                || blockEntity instanceof DecoratedPotBlockEntity
                || blockEntity instanceof BedBlockEntity;
                //|| blockEntity instanceof SignBlockEntity;
    }

    private boolean isAnimating() {
        float animationProgress = 0f;
        boolean animating = false;

        if (blockEntity instanceof LidOpenable lid)
            animationProgress = lid.getAnimationProgress(tickDelta);

        else if (blockEntity instanceof ShulkerBoxBlockEntity shulker)
            animationProgress = shulker.getAnimationProgress(tickDelta);

        else if (blockEntity instanceof BellBlockEntity bell)
            animating = bell.ringing;

        else if (blockEntity instanceof DecoratedPotBlockEntity pot && pot.lastWobbleType != null) {
            long now = blockEntity.getWorld().getTime();
            long wobbleTime = pot.lastWobbleTime;
            int lengthInTicks = pot.lastWobbleType.lengthInTicks;
            animating = now - wobbleTime < lengthInTicks;
        }

        return animationProgress > 0.0f || animating;
    }

    public boolean shouldRender() {
        if (!isSupportedEntity())
            return true;

        return isAnimating() ? handleAnimating() : handleStatic();
    }

    private boolean handleAnimating() {
        var pos = blockEntity.getPos();
        if (!BlockEntityTracker.animMap.contains(pos)) {
            BlockEntityTracker.animMap.add(pos);
            BlockEntityTracker.sectionsToUpdate.add(renderSection);
        }
        return true;
    }

    private boolean handleStatic() {
        var pos = blockEntity.getPos();

        if (BlockEntityTracker.animMap.remove(pos)) {
            BlockEntityTracker.sectionsToUpdate.add(renderSection);
            BlockEntityTracker.extraRenderPasses.put(pos, ConfigManager.CONFIG.smoothness_slider);
        }

        Integer passes = BlockEntityTracker.extraRenderPasses.get(pos);
        if (passes != null) {
            if (passes > 0) {
                BlockEntityTracker.extraRenderPasses.put(pos, passes - 1);
                return true;
            } else {
                BlockEntityTracker.extraRenderPasses.remove(pos);
            }
        }
        return false;
    }
}
