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
    public static Context Context;
    public static BlockStateModel bell_wall;
    public static BlockStateModel bell_floor;
    public static BlockStateModel bell_ceiling;
    public static BlockStateModel bell_between_walls;
    private static boolean modelsLoaded = false;

    public static final ExtraModelKey<BlockStateModel> BELL_WALL_KEY = ExtraModelKey.create(() -> "BellWallModel");
    public static final ExtraModelKey<BlockStateModel> BELL_FLOOR_KEY = ExtraModelKey.create(() -> "BellFloorModel");
    public static final ExtraModelKey<BlockStateModel> BELL_CEILING_KEY = ExtraModelKey.create(() -> "BellCeilingModel");
    public static final ExtraModelKey<BlockStateModel> BELL_BETWEEN_WALLS_KEY = ExtraModelKey.create(() -> "BellBetweenWallsModel");

    @Override
    public void initialize(Context pluginContext) {
        Context = pluginContext;
        var BELL_WALL_ID = Identifier.of("betterblockentities","block/bell_wall");
        var BELL_FLOOR_ID = Identifier.of("betterblockentities","block/bell_floor");
        var BELL_CEILING_ID = Identifier.of("betterblockentities","block/bell_ceiling");
        var BELL_BETWEEN_WALLS_ID = Identifier.of("betterblockentities","block/bell_between_walls");

        /* adds model which we can retrieve with bakedModelManager.getModel(key) */
        pluginContext.addModel(BELL_WALL_KEY, SimpleUnbakedExtraModel.blockStateModel(BELL_WALL_ID));
        pluginContext.addModel(BELL_FLOOR_KEY, SimpleUnbakedExtraModel.blockStateModel(BELL_FLOOR_ID));
        pluginContext.addModel(BELL_CEILING_KEY, SimpleUnbakedExtraModel.blockStateModel(BELL_CEILING_ID));
        pluginContext.addModel(BELL_BETWEEN_WALLS_KEY, SimpleUnbakedExtraModel.blockStateModel(BELL_BETWEEN_WALLS_ID));
    }

    public static void loadModels() {
        if (modelsLoaded)
            return;

        BakedModelManager manager = MinecraftClient.getInstance().getBakedModelManager();
        if (manager == null)
            return;

        bell_wall = manager.getModel(BELL_WALL_KEY);
        bell_floor = manager.getModel(BELL_FLOOR_KEY);
        bell_ceiling = manager.getModel(BELL_CEILING_KEY);
        bell_between_walls = manager.getModel(BELL_BETWEEN_WALLS_KEY);

        modelsLoaded = true;
    }
}
