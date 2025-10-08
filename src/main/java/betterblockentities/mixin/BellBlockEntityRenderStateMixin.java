package betterblockentities.mixin;

/* local */
import betterblockentities.helpers.BellRenderStateAccessor;

/* minecraft */
import net.minecraft.block.enums.Attachment;
import net.minecraft.client.render.block.entity.state.BellBlockEntityRenderState;
import net.minecraft.util.math.Direction;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BellBlockEntityRenderState.class)
public class BellBlockEntityRenderStateMixin implements BellRenderStateAccessor
{
    @Unique private Attachment mountAttachment;
    @Unique private Direction mountFacing;

    @Override public void setMountAttachment(Attachment attachment) {
        this.mountAttachment = attachment;
    }

    @Override public Attachment getMountAttachment() {
        return mountAttachment;
    }

    @Override public void setMountFacing(Direction direction) {
        this.mountFacing = direction;
    }

    @Override public Direction getMountFacing() {
        return mountFacing;
    }
}
