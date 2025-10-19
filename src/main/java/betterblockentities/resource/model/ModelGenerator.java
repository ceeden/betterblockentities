package betterblockentities.resource.model;

/* gson */
import betterblockentities.resource.model.models.*;
import betterblockentities.resource.model.util.TemplateLoader;
import com.google.gson.*;

/* minecraft */

/* java/misc */
import java.util.*;

public class ModelGenerator
{
    protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    protected static final String NAMESPACE = "minecraft";
    protected static final TemplateLoader loader = new TemplateLoader();

    /* generates all necessary block models and blockstate jsons for "chunk rendering" */
    public Map<String, byte[]> generateAllModels() {
        Map<String, byte[]> entries = new HashMap<>();

        /* chests */
        ChestModels.Model.generateLeftChests(entries);
        ChestModels.Model.generateRightChests(entries);
        ChestModels.Model.generateSingleChests(entries);
        ChestModels.BlockState.generateChestBlockstates(entries);

        /* beds */
        BedModels.Model.generateBedsHead(entries);
        BedModels.Model.generateBedsFoot(entries);
        BedModels.BlockState.generateBedBlockstates(entries);

        /* shulkers */
        ShulkerModels.Model.generateShulkerBase(entries);
        ShulkerModels.Model.generateShulkerLid(entries);
        ShulkerModels.BlockState.generateShulkerBlockstates(entries);

        /* signs */
        SignModels.Model.generateSignModels(entries);
        SignModels.BlockState.generateSignBlockstates(entries);
        return entries;
    }

    protected static JsonObject makeModel(String texKey, String texture, List<TemplateLoader.ElementRecord> elements) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", texture);
        textures.addProperty(texKey, texture);
        model.add("textures", textures);
        model.add("elements", GSON.toJsonTree(elements));
        return model;
    }

    protected static JsonObject createVariant(String name, int x, int y) {
        JsonObject v = new JsonObject();
        v.addProperty("model", NAMESPACE + ":block/" + name);
        if (x != 0) v.addProperty("x", x);
        if (y != 0) v.addProperty("y", y);
        return v;
    }

    protected static JsonObject createVariantFloat(String name, float x, float y) {
        JsonObject v = new JsonObject();
        v.addProperty("model", NAMESPACE + ":block/" + name);
        if (x != 0) v.addProperty("x", x);
        if (y != 0) v.addProperty("y", y);
        return v;
    }
}
