package betterblockentities;

/* fabric */
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.*;

/* minecraft */
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.Identifier;

/*
    this is our model-loader, it loads our (blockstate, model) JSON, links textures
    and bakes it into a nice BlockStateModel we can use

    -custom models go under assets/betterblockentities
    -don´t touch assets/minecraft unless you know what you
     are doing, these are what minecraft falls back on when
     we are setting BlockRenderType to MODEL and redirect it
     to be rendered in the chunk mesh
 */

@Environment(EnvType.CLIENT)
public class ModelLoader implements ModelLoadingPlugin
{
    /* key */
    public static final ExtraModelKey<BlockStateModel> CUSTOM_MODEL_KEY = ExtraModelKey.create(() -> "CUSTOM");

    @Override
    public void initialize(Context pluginContext)
    {
        /* namespace defaults to "assets/" */
        var CUSTOM_MODEL_ID = Identifier.of("betterblockentities", "block/CUSTOM_MODEL");

        /* adds model which we can retrieve with bakedModelManager.getModel(key) */
        //pluginContext.addModel(CUSTOM_MODEL_KEY, SimpleUnbakedExtraModel.blockStateModel(CUSTOM_MODEL_ID));
    }
}
