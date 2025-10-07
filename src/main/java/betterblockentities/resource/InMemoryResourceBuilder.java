package betterblockentities.resource;

import com.google.gson.*;
import net.minecraft.util.DyeColor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class InMemoryResourceBuilder
{
    private static final String NAMESPACE = "minecraft";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<String, byte[]> resourceEntries = new HashMap<>();
    private boolean mcmetaGenerated = false;

    public InMemoryResourceBuilder() {
        generatePackMcmeta();
    }

    // ======== Core Pack Metadata ========

    private void generatePackMcmeta() {
        if (mcmetaGenerated) return;

        JsonObject packJson = new JsonObject();
        JsonObject packInfo = new JsonObject();
        packInfo.addProperty("pack_format", 34); // adjust as needed for your MC version
        packInfo.addProperty("description", "BBE Models");
        packJson.add("pack", packInfo);

        resourceEntries.put("pack.mcmeta", GSON.toJson(packJson).getBytes(StandardCharsets.UTF_8));
        mcmetaGenerated = true;
    }

    // ======== Template Loader ========

    private JsonObject loadTemplate(String template) {
        try (InputStream input = ResourceBuilder.class.getResourceAsStream(
                "/assets/betterblockentities/models/block/" + template)) {
            if (input == null) return null;
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<ElementRecord> readTemplateElements(JsonObject templateRoot) {
        List<ElementRecord> elementList = new ArrayList<>();
        JsonArray elements = templateRoot.getAsJsonArray("elements");

        for (JsonElement el : elements) {
            JsonObject obj = el.getAsJsonObject();

            List<Float> from = jsonArrayToFloatList(obj.getAsJsonArray("from"));
            List<Float> to = jsonArrayToFloatList(obj.getAsJsonArray("to"));

            JsonObject faces = obj.getAsJsonObject("faces");
            Map<String, FaceRecord> faceMap = new HashMap<>();

            for (String face : faces.keySet()) {
                JsonObject faceData = faces.getAsJsonObject(face);
                List<Float> uv = faceData.has("uv") ? jsonArrayToFloatList(faceData.getAsJsonArray("uv")) : List.of();
                String texture = faceData.has("texture") ? faceData.get("texture").getAsString() : "";
                Float rotation = faceData.has("rotation") ? faceData.get("rotation").getAsFloat() : null;
                faceMap.put(face, new FaceRecord(uv, texture, rotation));
            }

            elementList.add(new ElementRecord(from, to, faceMap));
        }

        return elementList;
    }

    // ======== Model + Blockstate Generation ========

    public void generateShulkerBoxes() {
        JsonObject templateRoot = loadTemplate("shulker.json");
        if (templateRoot == null) return;
        List<ElementRecord> templateElements = readTemplateElements(templateRoot);

        for (DyeColor color : DyeColor.values()) {
            String blockModelName = color.getId() + "_shulker_box";
            String texturePath = "minecraft:entity/shulker/shulker_" + color.getId();

            JsonObject model = new JsonObject();
            model.addProperty("parent", "block/block");

            JsonObject textures = new JsonObject();
            textures.addProperty("particle", texturePath);
            textures.addProperty("shulker", texturePath);
            model.add("textures", textures);

            model.add("elements", GSON.toJsonTree(templateElements));

            String path = "assets/minecraft/models/block/" + blockModelName + ".json";
            resourceEntries.put(path, GSON.toJson(model).getBytes(StandardCharsets.UTF_8));
        }
    }

    public void generateLeftChests() {
        JsonObject templateRoot = loadTemplate("left_chest.json");
        if (templateRoot == null) return;
        List<ElementRecord> templateElements = readTemplateElements(templateRoot);

        Map<String, String> models = Map.of(
                "chest_left", "normal_left",
                "copper_chest_left", "copper_left",
                "exposed_copper_chest_left", "copper_exposed_left",
                "oxidized_copper_chest_left", "copper_oxidized_left",
                "trapped_chest_left", "trapped_left",
                "weathered_copper_chest_left", "copper_weathered_left"
        );

        for (Map.Entry<String, String> entry : models.entrySet()) {
            String texturePath = "minecraft:entity/chest/" + entry.getValue();
            String modelName = entry.getKey();

            JsonObject model = new JsonObject();
            model.addProperty("parent", "block/block");

            JsonObject textures = new JsonObject();
            textures.addProperty("particle", texturePath);
            textures.addProperty("chest", texturePath);
            model.add("textures", textures);

            model.add("elements", GSON.toJsonTree(templateElements));

            String path = "assets/minecraft/models/block/" + modelName + ".json";
            resourceEntries.put(path, GSON.toJson(model).getBytes(StandardCharsets.UTF_8));
        }
    }

    public void generateRightChests() {
        JsonObject templateRoot = loadTemplate("right_chest.json");
        if (templateRoot == null) return;
        List<ElementRecord> templateElements = readTemplateElements(templateRoot);

        Map<String, String> models = Map.of(
                "chest_right", "normal_right",
                "copper_chest_right", "copper_right",
                "exposed_copper_chest_right", "copper_exposed_right",
                "oxidized_copper_chest_right", "copper_oxidized_right",
                "trapped_chest_right", "trapped_right",
                "weathered_copper_chest_right", "copper_weathered_right"
        );

        for (Map.Entry<String, String> entry : models.entrySet()) {
            String texturePath = "minecraft:entity/chest/" + entry.getValue();
            String modelName = entry.getKey();

            JsonObject model = new JsonObject();
            model.addProperty("parent", "block/block");

            JsonObject textures = new JsonObject();
            textures.addProperty("particle", texturePath);
            textures.addProperty("chest", texturePath);
            model.add("textures", textures);

            model.add("elements", GSON.toJsonTree(templateElements));

            String path = "assets/minecraft/models/block/" + modelName + ".json";
            resourceEntries.put(path, GSON.toJson(model).getBytes(StandardCharsets.UTF_8));
        }
    }

    public void generateShulkerBlockstates() {
        for (DyeColor color : DyeColor.values()) {
            String blockName = color.getId() + "_shulker_box";

            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();

            variants.add("facing=up", createVariant(blockName, 0, 180));
            variants.add("facing=down", createVariant(blockName, 180, 180));
            variants.add("facing=north", createVariant(blockName, 90, 0));
            variants.add("facing=south", createVariant(blockName, 90, 180));
            variants.add("facing=west", createVariant(blockName, 90, 270));
            variants.add("facing=east", createVariant(blockName, 90, 90));

            blockstate.add("variants", variants);

            String path = "assets/minecraft/blockstates/" + blockName + ".json";
            resourceEntries.put(path, GSON.toJson(blockstate).getBytes(StandardCharsets.UTF_8));
        }
    }

    public void generateChestBlockstates() {
        HashSet<String> models = new HashSet<>(Arrays.asList(
                "chest",
                "copper_chest",
                "exposed_copper_chest",
                "oxidized_copper_chest",
                "trapped_chest",
                "weathered_copper_chest"
        ));

        for (String modelName : models) {
            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();

            String[] facings = {"north", "east", "south", "west"};
            int[] rotations = {0, 90, 180, 270};

            for (int i = 0; i < facings.length; i++) {
                String facing = facings[i];
                int y = rotations[i];

                variants.add(String.format("type=left,facing=%s", facing), createVariant(modelName + "_left", 0, y));
                variants.add(String.format("type=right,facing=%s", facing), createVariant(modelName + "_right", 0, y));
            }

            blockstate.add("variants", variants);
            String path = "assets/minecraft/blockstates/" + modelName + ".json";
            resourceEntries.put(path, GSON.toJson(blockstate).getBytes(StandardCharsets.UTF_8));
        }
    }

    // ======== Helpers ========

    private static JsonObject createVariant(String blockName, int x, int y) {
        JsonObject variant = new JsonObject();
        variant.addProperty("model", NAMESPACE + ":block/" + blockName);
        if (x != 0) variant.addProperty("x", x);
        if (y != 0) variant.addProperty("y", y);
        return variant;
    }

    private static List<Float> jsonArrayToFloatList(JsonArray arr) {
        List<Float> list = new ArrayList<>();
        for (JsonElement e : arr) list.add(e.getAsFloat());
        return list;
    }

    // ======== Pack Builder ========

    public byte[] buildZip() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(baos)) {

            for (Map.Entry<String, byte[]> entry : resourceEntries.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(entry.getKey());
                zip.putNextEntry(zipEntry);
                zip.write(entry.getValue());
                zip.closeEntry();
            }

            zip.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to build in-memory resource pack", e);
        }
    }

    // ======== Record Types ========

    public record ElementRecord(List<Float> from, List<Float> to, Map<String, FaceRecord> faces) {}
    public record FaceRecord(List<Float> uv, String texture, Float rotation) {}
}
