package betterblockentities.mixin.minecraft.chest;

/* minecraft */
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ChestLidAnimator;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChestBlockEntity.class)
public interface ChestBlockEntityAccessor {
    @Mutable @Accessor("lidAnimator")
    ChestLidAnimator getLidAnimator();

    @Mutable @Accessor("lidAnimator")
    void setLidAnimator(ChestLidAnimator animator);
}
