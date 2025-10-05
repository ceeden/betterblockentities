package betterblockentities.helpers;

import betterblockentities.gui.ConfigManager;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;

public class BlockEntityManager
{
    private static BlockEntity blockEntity;
    private static RenderSection renderSection;
    private static float tickDelta;

    public BlockEntityManager(BlockEntity blockEntity, RenderSection renderSection, float tickDelta)
    {
        this.blockEntity = blockEntity;
        this.renderSection = renderSection;
        this.tickDelta = tickDelta;
    }

    public static boolean blockSanityCheck(Block block)
    {
        if (block instanceof ChestBlock || block instanceof EnderChestBlock)
            return true;
        else if (block instanceof ShulkerBoxBlock)
            return true;
        else if (block instanceof BellBlock)
            return true;
        else if (block instanceof DecoratedPotBlock)
            return true;
        return false;
    }

    private boolean entitySanityCheck()
    {
        if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof EnderChestBlockEntity)
            return true;
        else if (blockEntity instanceof ShulkerBoxBlockEntity)
            return true;
        else if (blockEntity instanceof BellBlockEntity)
            return true;
        else if (blockEntity instanceof DecoratedPotBlockEntity)
            return true;
        return false;
    }

    private boolean isAnimating()
    {
        float animationProgress = 0;
        boolean animating = false;

        if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof EnderChestBlockEntity)
            animationProgress = ((LidOpenable)blockEntity).getAnimationProgress(tickDelta);
        else if (blockEntity instanceof ShulkerBoxBlockEntity)
            animationProgress = ((ShulkerBoxBlockEntity)blockEntity).getAnimationProgress(tickDelta);
        else if (blockEntity instanceof BellBlockEntity)
            animating = ((BellBlockEntity)blockEntity).ringing;
        else if (blockEntity instanceof DecoratedPotBlockEntity)
        {
            if (((DecoratedPotBlockEntity)blockEntity).lastWobbleType != null)
            {
                long now = blockEntity.getWorld().getTime();
                long wobble_time = ((DecoratedPotBlockEntity)blockEntity).lastWobbleTime;
                int lengthInTicks = ((DecoratedPotBlockEntity)blockEntity).lastWobbleType.lengthInTicks;
                animating = now - wobble_time < lengthInTicks;
            }
        }

        if (animationProgress > 0.00 || animating)
            return true;
        return false;
    }

    public boolean shouldRender()
    {
        //render normal blocks regularly
        if (!entitySanityCheck())
            return true;

        if (isAnimating()) {
            return animatingLogic();
        }
        else {
            return staticLogic();
        }
    }

    private boolean animatingLogic()
    {
        if (!(BlockEntityTracker.animMap.contains(blockEntity.getPos())))
        {
            BlockEntityTracker.animMap.add(blockEntity.getPos());
            BlockEntityTracker.sectionsToUpdate.add(renderSection);
        }
        return true;
    }

    private boolean staticLogic()
    {
        if (BlockEntityTracker.animMap.contains(blockEntity.getPos()))
        {
            BlockEntityTracker.animMap.remove(blockEntity.getPos());
            BlockEntityTracker.sectionsToUpdate.add(renderSection);
            BlockEntityTracker.extraRenderPasses.put(blockEntity.getPos(), ConfigManager.CONFIG.smoothness_slider);
        }
        if (BlockEntityTracker.extraRenderPasses.containsKey(blockEntity.getPos()))
        {
            int renderPasses = BlockEntityTracker.extraRenderPasses.get(blockEntity.getPos());
            if (renderPasses > 0)
            {
                BlockEntityTracker.extraRenderPasses.put(blockEntity.getPos(), renderPasses - 1);
                return true;
            }
            else {
                BlockEntityTracker.extraRenderPasses.remove(blockEntity.getPos());
            }
        }
        return false;
    }
}
