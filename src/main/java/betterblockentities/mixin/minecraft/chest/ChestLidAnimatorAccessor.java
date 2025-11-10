package betterblockentities.mixin.minecraft.chest;

/* minecraft */
import net.minecraft.block.entity.ChestLidAnimator;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChestLidAnimator.class)
public interface ChestLidAnimatorAccessor {
    @Accessor("open")
    boolean getOpen();
    @Accessor("open")
    void setOpen(boolean value);

    @Accessor("progress")
    float getProgress();
    @Accessor("progress")
    void setProgress(float value);

    @Accessor("lastProgress")
    float getLastProgress();
    @Accessor("lastProgress")
    void setLastProgress(float value);
}
