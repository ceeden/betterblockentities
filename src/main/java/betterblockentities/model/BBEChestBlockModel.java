package betterblockentities.model;

/* minecraft */
import net.minecraft.client.model.*;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;

public class BBEChestBlockModel extends ChestBlockModel {
    public BBEChestBlockModel(ModelPart root) {
        super(root);
        root.getChild("bottom").hidden = true;
        root.getChild("bottom").visible = false;
    }
}
