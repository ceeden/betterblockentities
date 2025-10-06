package betterblockentities;

/* fabric */
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.*;

/* minecraft */
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ModelLoader implements ModelLoadingPlugin
{
    public static List<BlockModelPart> bell_wall_parts = new ArrayList<>();
    public static List<BlockModelPart> bell_floor_parts = new ArrayList<>();
    public static List<BlockModelPart> bell_ceiling_parts = new ArrayList<>();
    public static List<BlockModelPart> bell_between_walls_parts = new ArrayList<>();

    private static final Random random = Random.create();

    private static boolean modelsLoaded = false;

    public static final ExtraModelKey<BlockStateModel> BELL_WALL_KEY = ExtraModelKey.create(() -> "BellWallModel");
    public static final ExtraModelKey<BlockStateModel> BELL_FLOOR_KEY = ExtraModelKey.create(() -> "BellFloorModel");
    public static final ExtraModelKey<BlockStateModel> BELL_CEILING_KEY = ExtraModelKey.create(() -> "BellCeilingModel");
    public static final ExtraModelKey<BlockStateModel> BELL_BETWEEN_WALLS_KEY = ExtraModelKey.create(() -> "BellBetweenWallsModel");

    @Override
    public void initialize(Context pluginContext)
    {
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

    private static void getQuads(BlockStateModel model, List<BakedQuad> quadList) {
        for (BlockModelPart part : model.getParts(random))
        {
            for (Direction dir : Direction.values())
            {
                List<BakedQuad> faceQuads = part.getQuads(dir);
                if (faceQuads != null && !faceQuads.isEmpty()) {
                    quadList.addAll(faceQuads);
                }
            }
            List<BakedQuad> sideless = part.getQuads(null);
            if (sideless != null && !sideless.isEmpty()) {
                quadList.addAll(sideless);
            }
        }
    }

    private static void getParts(BlockStateModel model, List<BlockModelPart> partList) {
        partList.addAll(model.getParts(random));
    }

    public static void loadModels()
    {
        if (modelsLoaded)
            return;

        BakedModelManager manager = MinecraftClient.getInstance().getBakedModelManager();
        if (manager == null)
            return;

        BlockStateModel bell_wall = manager.getModel(BELL_WALL_KEY);
        BlockStateModel bell_floor = manager.getModel(BELL_FLOOR_KEY);
        BlockStateModel bell_ceiling = manager.getModel(BELL_CEILING_KEY);
        BlockStateModel bell_between_walls = manager.getModel(BELL_BETWEEN_WALLS_KEY);

        getParts(bell_wall, bell_wall_parts);
        getParts(bell_floor, bell_floor_parts);
        getParts(bell_ceiling, bell_ceiling_parts);
        getParts(bell_between_walls, bell_between_walls_parts);

        modelsLoaded = true;
    }
}
