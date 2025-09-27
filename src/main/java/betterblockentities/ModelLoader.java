package betterblockentities;

/* fabric */
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.*;

/* minecraft */
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelLoader implements ModelLoadingPlugin
{
    public static final ExtraModelKey<BlockStateModel> CUSTOM_MODEL_KEY = ExtraModelKey.create(() -> "CUSTOM");

    @Override
    public void initialize(Context pluginContext)
    {
        var CUSTOM_MODEL_ID = Identifier.of("betterblockentities", "block/CUSTOM_MODEL");

        /* adds model which we can retrieve with bakedModelManager.getModel(key) */
        //pluginContext.addModel(CUSTOM_MODEL_KEY, SimpleUnbakedExtraModel.blockStateModel(CUSTOM_MODEL_ID));
    }
}
