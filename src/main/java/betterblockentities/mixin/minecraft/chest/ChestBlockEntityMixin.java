package betterblockentities.mixin.minecraft.chest;

/* local */
import betterblockentities.util.BlockEntityExt;

/* minecraft */
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;

/* mixin */
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin {
    /* only run tick logic when we receive a block event */
    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private static void onTick(World world, BlockPos pos, BlockState state, ChestBlockEntity blockEntity, CallbackInfo ci) {
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
