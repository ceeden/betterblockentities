package betterblockentities.helpers;

import net.minecraft.block.BellBlock;
import net.minecraft.block.enums.Attachment;
import net.minecraft.util.math.Direction;

public interface BellRenderStateAccessor
{
    void setMountAttachment(Attachment attachment);
    Attachment getMountAttachment();

    void setMountFacing(Direction direction);
    Direction getMountFacing();
}
