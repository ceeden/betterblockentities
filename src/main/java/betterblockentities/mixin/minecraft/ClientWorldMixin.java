package betterblockentities.mixin.minecraft;

/* local */
import betterblockentities.gui.ConfigManager;
import betterblockentities.mixin.minecraft.chest.ChestBlockEntityAccessor;
import betterblockentities.mixin.minecraft.chest.ChestLidAnimatorAccessor;
import betterblockentities.util.BlockEntityExt;
import betterblockentities.util.BlockEntityManager;
import betterblockentities.util.BlockEntityTracker;

/* fabric */
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/* minecraft */
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "handleBlockUpdate", at = @At("TAIL"), cancellable = true)
    public void handleBlockUpdate(BlockPos pos, BlockState state, int flags, CallbackInfo ci) {
        if (!ConfigManager.CONFIG.master_optimize)
            return;

        ClientWorld world = (ClientWorld)(Object)this;

        if (!world.isClient()) return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) return;

        if (!BlockEntityManager.isSupportedEntity(blockEntity)) return;

        BlockEntityExt inst = (BlockEntityExt) blockEntity;
        if (inst == null) return;

        /* get other chest half */
        BlockEntity alt = getOtherChestHalf(blockEntity.getWorld(), blockEntity.getPos());

        /* sync other half */
        if (alt != null && ((LidOpenable)alt).getAnimationProgress(0.5f) > 0f) {
            ChestLidAnimator src = ((ChestBlockEntityAccessor)alt).getLidAnimator();
            ChestLidAnimator dst = ((ChestBlockEntityAccessor)blockEntity).getLidAnimator();

            ChestLidAnimatorAccessor accSrc = ((ChestLidAnimatorAccessor)src);
            ChestLidAnimatorAccessor accDst = ((ChestLidAnimatorAccessor)dst);

            accDst.setOpen(accSrc.getOpen());
            accDst.setProgress(accSrc.getProgress());
            accDst.setLastProgress(accSrc.getLastProgress());

            inst.setJustReceivedUpdate(true);
        }
    }

    @Unique
    private static ChestBlockEntity getOtherChestHalf(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof ChestBlock)) return null;

        ChestType type = state.get(ChestBlock.CHEST_TYPE);
        Direction facing = state.get(ChestBlock.FACING);

        Direction side;
        if (type == ChestType.LEFT) {
            side = facing.rotateYClockwise();
        } else if (type == ChestType.RIGHT) {
            side = facing.rotateYCounterclockwise();
        } else {
            return null;
        }

        BlockPos otherPos = pos.offset(side);
        BlockEntity be = world.getBlockEntity(otherPos);

        return be instanceof ChestBlockEntity ? (ChestBlockEntity) be : null;
    }
}
