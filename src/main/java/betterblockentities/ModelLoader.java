package betterblockentities;

/* fabric */
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.*;

/* minecraft */
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.Identifier;

import java.util.HashSet;

@Environment(EnvType.CLIENT)
public class ModelLoader implements ModelLoadingPlugin
{
    public static BlockStateModel bell_body;
    private static boolean modelsLoaded = false;

    public static final ExtraModelKey<BlockStateModel> BELL_BODY_KEY = ExtraModelKey.create(() -> "BellBody");

    @Override
    public void initialize(Context pluginContext)
    {
        var BELL_BODY_ID = Identifier.of("betterblockentities","block/bell_body");

        /* adds model which we can retrieve with bakedModelManager.getModel(key) */
        pluginContext.addModel(BELL_BODY_KEY, SimpleUnbakedExtraModel.blockStateModel(BELL_BODY_ID));
    }

    public static void loadModels() {
        if (modelsLoaded)
            return;

        BakedModelManager manager = MinecraftClient.getInstance().getBakedModelManager();
        if (manager == null)
            return;

        bell_body = manager.getModel(BELL_BODY_KEY);

        modelsLoaded = true;
    }
}
