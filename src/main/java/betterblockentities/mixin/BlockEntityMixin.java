package betterblockentities.mixin;

/* local */
import betterblockentities.util.BlockEntityExt;

/* minecraft */
import net.minecraft.block.entity.BlockEntity;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityExt {
    @Unique private boolean justReceivedUpdate = false;
    @Unique private boolean shouldRemoveChunkVariant = false;

    @Override public boolean getJustReceivedUpdate() { return justReceivedUpdate;}
    @Override public void setJustReceivedUpdate(boolean value) { this.justReceivedUpdate = value; }

    @Override public void setRemoveChunkVariant(boolean value) { this.shouldRemoveChunkVariant = value; }
    @Override public boolean getRemoveChunkVariant() {return this.shouldRemoveChunkVariant; }
}