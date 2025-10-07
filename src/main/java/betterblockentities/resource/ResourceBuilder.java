package betterblockentities.resource;

import com.google.gson.JsonParser;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ResourceBuilder
{
    private static final String MOD_ID = "betterblockentities";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File MODEL_DIR = new File("run/generated_resources/assets/" + MOD_ID + "/models/block/");
    private static final File BLOCKSTATE_DIR = new File("run/generated_resources/assets/" + MOD_ID + "/blockstates/");
    private static final File TEMPLATE_FILE = new File(MODEL_DIR, "shulker_template.json");
    private static boolean dirsBuilt = false;

    public ResourceBuilder() {
        createResourceDirectories();
    }

    private void createResourceDirectories() {
        if (!dirsBuilt) {
            MODEL_DIR.mkdirs();
            BLOCKSTATE_DIR.mkdirs();
            dirsBuilt = true;
        }
    }

    private static void copyTemplateIfMissing()
    {
        if (TEMPLATE_FILE.exists()) return;

        try (InputStream input = ResourceBuilder.class.getResourceAsStream(
                "/assets/" + MOD_ID + "/models/block/shulker.json")) {

            if (input == null)
                return;

            TEMPLATE_FILE.getParentFile().mkdirs();
            try (OutputStream out = new FileOutputStream(TEMPLATE_FILE)) {
                input.transferTo(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Reads "elements" array from a template file and returns parsed ElementRecords */
    public static List<ElementRecord> readTemplateElements(File template)
    {
        List<ElementRecord> elementList = new ArrayList<>();
        try (FileReader reader = new FileReader(template)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray elements = root.getAsJsonArray("elements");

            for (JsonElement el : elements) {
                JsonObject obj = el.getAsJsonObject();

                List<Float> from = jsonArrayToFloatList(obj.getAsJsonArray("from"));
                List<Float> to = jsonArrayToFloatList(obj.getAsJsonArray("to"));

                JsonObject faces = obj.getAsJsonObject("faces");
                Map<String, FaceRecord> faceMap = new HashMap<>();

                for (String face : faces.keySet()) {
                    JsonObject faceData = faces.getAsJsonObject(face);

                    List<Float> uv = faceData.has("uv")
                            ? jsonArrayToFloatList(faceData.getAsJsonArray("uv"))
                            : List.of();

                    String texture = faceData.has("texture")
                            ? faceData.get("texture").getAsString()
                            : "";

                    faceMap.put(face, new FaceRecord(uv, texture));
                }

                elementList.add(new ElementRecord(from, to, faceMap));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return elementList;
    }

    /* Generates all colored shulker box models based on the template */
    public void generateShulkerBoxes()
    {
        createResourceDirectories();
        copyTemplateIfMissing();

        if (!TEMPLATE_FILE.exists()) {
            return;
        }

        List<ElementRecord> templateElements = readTemplateElements(TEMPLATE_FILE);

        for (DyeColor color : DyeColor.values()) {
            String blockModelName = color.getId() + "_shulker_box";
            String texturePath = "minecraft:entity/shulker/shulker_" + color.getId();

            JsonObject model = new JsonObject();
            model.addProperty("parent", "block/block");

            JsonObject textures = new JsonObject();
            textures.addProperty("particle", texturePath);
            textures.addProperty("shulker", texturePath);
            model.add("textures", textures);

            // Add elements from template
            model.add("elements", GSON.toJsonTree(templateElements));

            File outFile = new File(MODEL_DIR, blockModelName + ".json");
            try (FileWriter writer = new FileWriter(outFile)) {
                GSON.toJson(model, writer);
                System.out.println("✅ Generated model: " + outFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Converts a JsonArray to a List<Float> */
    private static List<Float> jsonArrayToFloatList(JsonArray arr) {
        List<Float> list = new ArrayList<>();
        for (JsonElement e : arr) {
            list.add(e.getAsFloat());
        }
        return list;
    }

    // Records for model data
    public record ElementRecord(List<Float> from, List<Float> to, Map<String, FaceRecord> faces) {}
    public record FaceRecord(List<Float> uv, String texture) {}
}