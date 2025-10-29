package betterblockentities.mixin.sodium;

/* local */
import betterblockentities.util.BlockEntityManager;

/* sodium */
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;

/* minecraft */
import net.minecraft.block.entity.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
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
public abstract class SodiumWorldRendererMixin {
    @Inject(method = "renderBlockEntity", at = @At("HEAD"), cancellable = true)
    private static void renderBlockEntity(MatrixStack matrices, BufferBuilderStorage bufferBuilders, Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions, float tickDelta, VertexConsumerProvider.Immediate immediate, double x, double y, double z, BlockEntityRenderDispatcher dispatcher, BlockEntity entity, ClientPlayerEntity player, LocalBooleanRef isGlowing, CallbackInfo ci) {
        if (!BlockEntityManager.shouldRender(entity))
            ci.cancel();
    }
}