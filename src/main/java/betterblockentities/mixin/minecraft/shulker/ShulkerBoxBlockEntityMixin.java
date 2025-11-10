package betterblockentities.mixin.minecraft.shulker;

/* local */
import betterblockentities.util.BlockEntityExt;

/* minecraft */
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;

/* mixin */
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin {
    /* only run tick logic when we receive a block event */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private static void onTick(World world, BlockPos pos, BlockState state, ShulkerBoxBlockEntity blockEntity, CallbackInfo ci) {
        if (!(((BlockEntityExt)blockEntity).getJustReceivedUpdate()))
            ci.cancel();
    }

    /* capture block event for conditional rendering in BlockEntityManager */
    @Inject(method = "onSyncedBlockEvent", at = @At("HEAD"), cancellable = true)
    private void onBlockEvent(int type, int data, CallbackInfoReturnable<Boolean> cir) {
        if (type != 1) return;
        ((BlockEntityExt)this).setJustReceivedUpdate(true);
    }
}
