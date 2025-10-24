package betterblockentities.helpers;

/* local */
import betterblockentities.gui.ConfigHolder;
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
        return blockEntity instanceof ChestBlockEntity && ConfigManager.CONFIG.optimize_chests
                || blockEntity instanceof EnderChestBlockEntity && ConfigManager.CONFIG.optimize_chests
                || blockEntity instanceof ShulkerBoxBlockEntity && ConfigManager.CONFIG.optimize_shulkers
                || blockEntity instanceof BellBlockEntity && ConfigManager.CONFIG.optimize_bells
                || blockEntity instanceof DecoratedPotBlockEntity && ConfigManager.CONFIG.optimize_decoratedpots
                || blockEntity instanceof BedBlockEntity && ConfigManager.CONFIG.optimize_beds
                || blockEntity instanceof SignBlockEntity && ConfigManager.CONFIG.optimize_signs;
    }

    private boolean isAnimating() {
        float animationProgress = 0f;
        boolean animating = false;

        if (blockEntity instanceof LidOpenable lid) {
            if (ConfigManager.CONFIG.chest_animations)
                animationProgress = lid.getAnimationProgress(tickDelta);
        }
        else if (blockEntity instanceof ShulkerBoxBlockEntity shulker) {
            if (ConfigManager.CONFIG.shulker_animations)
                animationProgress = shulker.getAnimationProgress(tickDelta);
        }
        else if (blockEntity instanceof BellBlockEntity bell) {
            if (ConfigManager.CONFIG.bell_animations)
                animating = bell.ringing;
        }
        else if (blockEntity instanceof DecoratedPotBlockEntity pot && pot.lastWobbleType != null) {
            if (ConfigManager.CONFIG.pot_animations) {
                long now = blockEntity.getWorld().getTime();
                long wobbleTime = pot.lastWobbleTime;
                int lengthInTicks = pot.lastWobbleType.lengthInTicks;
                animating = now - wobbleTime < lengthInTicks;
            }
        }
        else if (blockEntity instanceof SignBlockEntity) {
            if (ConfigManager.CONFIG.render_sign_text)
                animating = true;
        }
        return animationProgress > 0.0f || animating;
    }

    public boolean shouldRender() {
        if (!ConfigManager.CONFIG.master_optimize)
            return true;

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
