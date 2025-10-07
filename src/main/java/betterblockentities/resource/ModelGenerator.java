package betterblockentities.resource;

import com.google.gson.*;
import net.minecraft.util.DyeColor;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ModelGenerator
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String NAMESPACE = "minecraft";

    private final TemplateLoader loader = new TemplateLoader();

    public Map<String, byte[]> generateAllModels() {
        Map<String, byte[]> entries = new HashMap<>();
        generateShulkerBoxes(entries);
        generateLeftChests(entries);
        generateRightChests(entries);
        generateShulkerBlockstates(entries);
        generateChestBlockstates(entries);
        return entries;
    }

    // --- same as before, but writing into the provided map ---
    private void generateShulkerBoxes(Map<String, byte[]> map) {
        JsonObject template = loader.loadTemplate("shulker.json");
        if (template == null) return;
        var elements = loader.readTemplateElements(template);

        for (DyeColor color : DyeColor.values()) {
            String name = color.getId() + "_shulker_box";
            String texture = "minecraft:entity/shulker/shulker_" + color.getId();
            map.put("assets/minecraft/models/block/" + name + ".json",
                    GSON.toJson(makeModel("shulker", texture, elements)).getBytes(StandardCharsets.UTF_8));
        }
    }

    private void generateLeftChests(Map<String, byte[]> map) {
        generateChestSide(map, "left_chest.json", "_left", Map.of(
                "chest_left", "normal_left",
                "copper_chest_left", "copper_left",
                "exposed_copper_chest_left", "copper_exposed_left",
                "oxidized_copper_chest_left", "copper_oxidized_left",
                "trapped_chest_left", "trapped_left",
                "weathered_copper_chest_left", "copper_weathered_left"
        ));
    }

    private void generateRightChests(Map<String, byte[]> map) {
        generateChestSide(map, "right_chest.json", "_right", Map.of(
                "chest_right", "normal_right",
                "copper_chest_right", "copper_right",
                "exposed_copper_chest_right", "copper_exposed_right",
                "oxidized_copper_chest_right", "copper_oxidized_right",
                "trapped_chest_right", "trapped_right",
                "weathered_copper_chest_right", "copper_weathered_right"
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
    }

    private void generateChestBlockstates(Map<String, byte[]> map) {
        Set<String> names = Set.of("chest", "copper_chest", "exposed_copper_chest",
                "oxidized_copper_chest", "trapped_chest", "weathered_copper_chest");

        for (String name : names) {
            JsonObject variants = new JsonObject();
            String[] facings = {"north", "east", "south", "west"};
            int[] rotations = {0, 90, 180, 270};

            for (int i = 0; i < facings.length; i++) {
                String facing = facings[i];
                int y = rotations[i];
                variants.add("type=left,facing=" + facing, createVariant(name + "_left", 0, y));
                variants.add("type=right,facing=" + facing, createVariant(name + "_right", 0, y));
            }

            JsonObject root = new JsonObject();
            root.add("variants", variants);
            map.put("assets/minecraft/blockstates/" + name + ".json",
                    GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        }
    }

    private JsonObject createVariant(String name, int x, int y) {
        JsonObject v = new JsonObject();
        v.addProperty("model", NAMESPACE + ":block/" + name);
        if (x != 0) v.addProperty("x", x);
        if (y != 0) v.addProperty("y", y);
        return v;
    }
}
