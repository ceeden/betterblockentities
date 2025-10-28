package betterblockentities.mixin.sodium;

/* local */
import betterblockentities.util.BlockEntityManager;

/* sodium */
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;

/* minecraft */
import net.minecraft.block.entity.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.BlockBreakingInfo;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* java/misc */
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.SortedSet;

@Pseudo
@Mixin(SodiumWorldRenderer.class)
public abstract class SodiumWorldRendererMixin
{
    @Inject(method = "extractBlockEntity", at = @At("HEAD"), cancellable = true)
    private void extractBlockEntity(BlockEntity blockEntity, MatrixStack poseStack, Camera camera, float tickDelta, Long2ObjectMap<SortedSet<BlockBreakingInfo>> progression, WorldRenderState levelRenderState, CallbackInfo ci) {
        if (!BlockEntityManager.shouldRender(blockEntity))
            ci.cancel();
    }
}