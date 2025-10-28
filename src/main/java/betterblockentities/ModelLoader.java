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

@Environment(EnvType.CLIENT)
public class ModelLoader implements ModelLoadingPlugin
{
    public static BlockStateModel bell_body;
    private static boolean modelsLoaded = false;
    public static Context context;

    public static final ExtraModelKey<BlockStateModel> BELL_BODY_KEY = ExtraModelKey.create(() -> "BellBody");
    public static Identifier BELL_BODY_ID;

    @Override
    public void initialize(Context pluginContext) {
        context = pluginContext;

        BELL_BODY_ID = Identifier.of("betterblockentities", "block/bell_body");
        pluginContext.addModel(BELL_BODY_KEY, SimpleUnbakedExtraModel.blockStateModel(BELL_BODY_ID));
    }

    public static void loadModels() {
        if (modelsLoaded) return;

        BakedModelManager manager = MinecraftClient.getInstance().getBakedModelManager();
        if (manager == null) return;

        bell_body = manager.getModel(BELL_BODY_KEY);
        modelsLoaded = true;
    }
}
