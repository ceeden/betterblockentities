package betterblockentities.model;

/* minecraft */
import betterblockentities.gui.ConfigManager;
import net.minecraft.client.model.*;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;

public class BBEChestBlockModel extends ChestBlockModel {
    public BBEChestBlockModel(ModelPart root) {
        super(root);

        if (ConfigManager.CONFIG.updateType == 1) {
            root.getChild("bottom").hidden = true;
            root.getChild("bottom").visible = false;
        }

        //root.getChild("lid").hidden = true;
        //root.getChild("lid").visible = false;
    }
}
