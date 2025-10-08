package betterblockentities.resource;

/* gson */
import com.google.gson.*;

/* minecraft */
import net.minecraft.util.DyeColor;

/* java/misc */
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ModelGenerator
{
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final String NAMESPACE = "minecraft";

    private final TemplateLoader loader = new TemplateLoader();

    public Map<String, byte[]> generateAllModels() {
        Map<String, byte[]> entries = new HashMap<>();
        generateShulkerBoxes(entries);
        generateLeftChests(entries);
        generateRightChests(entries);
        generateSingleChests(entries);
        generateShulkerBlockstates(entries);
        generateChestBlockstates(entries);
        return entries;
    }

    private void generateShulkerBoxes(Map<String, byte[]> map) {
        JsonObject template = loader.loadTemplate("shulker_template.json");
        if (template == null) return;
        var elements = loader.readTemplateElements(template);

        for (DyeColor color : DyeColor.values()) {
            String name = color.getId() + "_shulker_box";
            String texture = "minecraft:entity/shulker/shulker_" + color.getId();
            map.put("assets/minecraft/models/block/" + name + ".json",
                    GSON.toJson(makeModel("shulker", texture, elements)).getBytes(StandardCharsets.UTF_8));
        }

        String baseName = "shulker_box";
        String baseTexture = "minecraft:entity/shulker/shulker";
        map.put("assets/minecraft/models/block/" + baseName + ".json",
                GSON.toJson(makeModel("shulker", baseTexture, elements)).getBytes(StandardCharsets.UTF_8));
    }

    //map is map of block model names and the texture it points to
    //example : "chest_left" -> "normal_left" where chest_left is the
    //block model name and normal_left is the texture name
    private void generateLeftChests(Map<String, byte[]> map) {
        generateChestSide(map, "left_chest_template.json", "_left", Map.of(
                "chest_left", "normal_left",
                "copper_chest_left", "copper_left",
                "exposed_copper_chest_left", "copper_exposed_left",
                "oxidized_copper_chest_left", "copper_oxidized_left",
                "trapped_chest_left", "trapped_left",
                "weathered_copper_chest_left", "copper_weathered_left"
        ));
    }

    private void generateRightChests(Map<String, byte[]> map) {
        generateChestSide(map, "right_chest_template.json", "_right", Map.of(
                "chest_right", "normal_right",
                "copper_chest_right", "copper_right",
                "exposed_copper_chest_right", "copper_exposed_right",
                "oxidized_copper_chest_right", "copper_oxidized_right",
                "trapped_chest_right", "trapped_right",
                "weathered_copper_chest_right", "copper_weathered_right"
        ));
    }

    private void generateSingleChests(Map<String, byte[]> map) {
        generateChestSide(map, "chest_template.json", "_single", Map.of(
                "chest", "normal",
                "copper_chest", "copper",
                "exposed_copper_chest", "copper_exposed",
                "oxidized_copper_chest", "copper_oxidized",
                "trapped_chest", "trapped",
                "weathered_copper_chest", "copper_weathered",
                "ender_chest", "ender"
        ));
    }

    private void generateChestSide(Map<String, byte[]> map, String templateName, String suffix, Map<String, String> textureMap) {
        JsonObject template = loader.loadTemplate(templateName);
        if (template == null) return;
        var elements = loader.readTemplateElements(template);

        for (var entry : textureMap.entrySet()) {
            String model = entry.getKey();
            String texture = "minecraft:entity/chest/" + entry.getValue();
            map.put("assets/minecraft/models/block/" + model + ".json",
                    GSON.toJson(makeModel("chest", texture, elements)).getBytes(StandardCharsets.UTF_8));
        }
    }

    private void generateShulkerBlockstates(Map<String, byte[]> map) {
        for (DyeColor color : DyeColor.values()) {
            String name = color.getId() + "_shulker_box";
            JsonObject variants = new JsonObject();
            variants.add("facing=up", createVariant(name, 0, 180));
            variants.add("facing=down", createVariant(name, 180, 180));
            variants.add("facing=north", createVariant(name, 90, 0));
            variants.add("facing=south", createVariant(name, 90, 180));
            variants.add("facing=west", createVariant(name, 90, 270));
            variants.add("facing=east", createVariant(name, 90, 90));

            JsonObject root = new JsonObject();
            root.add("variants", variants);
            map.put("assets/minecraft/blockstates/" + name + ".json",
                    GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        }

        String baseName = "shulker_box";
        JsonObject baseVariants = new JsonObject();
        baseVariants.add("facing=up", createVariant(baseName, 0, 180));
        baseVariants.add("facing=down", createVariant(baseName, 180, 180));
        baseVariants.add("facing=north", createVariant(baseName, 90, 0));
        baseVariants.add("facing=south", createVariant(baseName, 90, 180));
        baseVariants.add("facing=west", createVariant(baseName, 90, 270));
        baseVariants.add("facing=east", createVariant(baseName, 90, 90));

        JsonObject baseRoot = new JsonObject();
        baseRoot.add("variants", baseVariants);
        map.put("assets/minecraft/blockstates/" + baseName + ".json",
                GSON.toJson(baseRoot).getBytes(StandardCharsets.UTF_8));
    }

    private void generateChestBlockstates(Map<String, byte[]> map) {
        Set<String> names = Set.of(
                "chest", "copper_chest", "exposed_copper_chest",
                "oxidized_copper_chest", "trapped_chest", "weathered_copper_chest",
                "ender_chest", "waxed_exposed_copper_chest", "waxed_copper_chest",
                "waxed_oxidized_copper_chest", "waxed_weathered_copper_chest"
        );

        for (String name : names) {
            JsonObject variants = new JsonObject();
            String[] facings = {"north", "east", "south", "west"};

            int[] rotations = {0, 90, 180, 270};
            for (int i = 0; i < facings.length; i++) {
                String facing = facings[i];
                int y = rotations[i];

                if (name.equals("ender_chest")) {
                    variants.add("facing=" + facing, createVariant(name, 0, y));
                } else {
                    String tempName = name;
                    if (tempName.contains("waxed_"))
                        tempName = tempName.replaceFirst("waxed_", "");

                    variants.add("type=left,facing=" + facing, createVariant(tempName + "_left", 0, y));
                    variants.add("type=right,facing=" + facing, createVariant(tempName + "_right", 0, y));
                    variants.add("type=single,facing=" + facing, createVariant(tempName, 0, y));
                }
            }

            JsonObject root = new JsonObject();
            root.add("variants", variants);
            map.put("assets/minecraft/blockstates/" + name + ".json",
                    GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        }
    }

    private JsonObject makeModel(String texKey, String texture, List<TemplateLoader.ElementRecord> elements) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", texture);
        textures.addProperty(texKey, texture);
        model.add("textures", textures);
        model.add("elements", GSON.toJsonTree(elements));
        return model;
    }

    private JsonObject createVariant(String name, int x, int y) {
        JsonObject v = new JsonObject();
        v.addProperty("model", NAMESPACE + ":block/" + name);
        if (x != 0) v.addProperty("x", x);
        if (y != 0) v.addProperty("y", y);
        return v;
    }
}
