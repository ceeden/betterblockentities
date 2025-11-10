package betterblockentities.mixin.minecraft.decordatedpot;

/* local */
import betterblockentities.util.BlockEntityExt;

/* minecraft */
import net.minecraft.block.entity.DecoratedPotBlockEntity;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotBlockEntity.class)
public class DecoratedPotBlockEntityMixin {
    /* capture block event for conditional rendering in BlockEntityManager */
    @Inject(method = "onSyncedBlockEvent", at = @At(value = "RETURN", shift = At.Shift.BEFORE, ordinal = 0))
    private void onBlockEvent(int type, int data, CallbackInfoReturnable<Boolean> cir) {
        ((BlockEntityExt)this).setJustReceivedUpdate(true);
    }
}
