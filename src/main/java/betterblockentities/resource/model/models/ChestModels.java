package betterblockentities.resource.model.models;

import betterblockentities.resource.model.ModelGenerator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;


/*
    generates blockstates(multipart) jsons for each chest variant (which will create a model type of
    MultiPartBlockStateModel during baking)

    generates block models for each chest variant (trunk, lid) (variant{left, right, single})
*/
public class ChestModels extends ModelGenerator {
    static Set<String> names = Set.of(
            "chest", "copper_chest", "exposed_copper_chest",
            "oxidized_copper_chest", "trapped_chest", "weathered_copper_chest",
            "ender_chest", "waxed_exposed_copper_chest", "waxed_copper_chest",
            "waxed_oxidized_copper_chest", "waxed_weathered_copper_chest"
    );


    public static class Model {
        public static void generateLeftChests(Map<String, byte[]> map) {
            generateChestSide(map, "left_chest_template.json", "_left", Map.of(
                    "chest_left_lid", "normal_left",
                    "copper_chest_left_lid", "copper_left",
                    "exposed_copper_chest_left_lid", "copper_exposed_left",
                    "oxidized_copper_chest_left_lid", "copper_oxidized_left",
                    "trapped_chest_left_lid", "trapped_left",
                    "weathered_copper_chest_left_lid", "copper_weathered_left"
            ));

            generateChestSide(map, "left_chest_lid_template.json", "_left", Map.of(
                    "chest_left", "normal_left",
                    "copper_chest_left", "copper_left",
                    "exposed_copper_chest_left", "copper_exposed_left",
                    "oxidized_copper_chest_left", "copper_oxidized_left",
                    "trapped_chest_left", "trapped_left",
                    "weathered_copper_chest_left", "copper_weathered_left"
            ));
        }

        public static void generateRightChests(Map<String, byte[]> map) {
            generateChestSide(map, "right_chest_template.json", "_right", Map.of(
                    "chest_right", "normal_right",
                    "copper_chest_right", "copper_right",
                    "exposed_copper_chest_right", "copper_exposed_right",
                    "oxidized_copper_chest_right", "copper_oxidized_right",
                    "trapped_chest_right", "trapped_right",
                    "weathered_copper_chest_right", "copper_weathered_right"
            ));

            generateChestSide(map, "right_chest_lid_template.json", "_right", Map.of(
                    "chest_right_lid", "normal_right",
                    "copper_chest_right_lid", "copper_right",
                    "exposed_copper_chest_right_lid", "copper_exposed_right",
                    "oxidized_copper_chest_right_lid", "copper_oxidized_right",
                    "trapped_chest_right_lid", "trapped_right",
                    "weathered_copper_chest_right_lid", "copper_weathered_right"
            ));
        }

        public static void generateSingleChests(Map<String, byte[]> map) {
            generateChestSide(map, "chest_template.json", "_single", Map.of(
                    "chest", "normal",
                    "copper_chest", "copper",
                    "exposed_copper_chest", "copper_exposed",
                    "oxidized_copper_chest", "copper_oxidized",
                    "trapped_chest", "trapped",
                    "weathered_copper_chest", "copper_weathered",
                    "ender_chest", "ender"
            ));

            generateChestSide(map, "chest_lid_template.json", "_single", Map.of(
                    "chest_lid", "normal",
                    "copper_chest_lid", "copper",
                    "exposed_copper_chest_lid", "copper_exposed",
                    "oxidized_copper_chest_lid", "copper_oxidized",
                    "trapped_chest_lid", "trapped",
                    "weathered_copper_chest_lid", "copper_weathered",
                    "ender_chest_lid", "ender"
            ));
        }

        private static void generateChestSide(Map<String, byte[]> map, String templateName, String suffix, Map<String, String> textureMap) {
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
    }

    public static class BlockState {
        public static void generateChestBlockstates(Map<String, byte[]> map) {
            Map<String, Integer> facingRot = Map.of(
                    "south", 180,
                    "west", 270,
                    "north", 0,
                    "east", 90
            );

            for (String name : names) {
                boolean isEnder = name.equals("ender_chest");
                String baseName = name.replaceFirst("^waxed_", "");

                JsonArray multipart = new JsonArray();

                for (var entry : facingRot.entrySet()) {
                    String facing = entry.getKey();
                    int yRot = entry.getValue();

                    if (isEnder) {
                        multipart.add(makeChestPart(baseName, "single", facing, yRot, isEnder));
                        multipart.add(makeChestPart(baseName + "_lid", "single", facing, yRot, isEnder));
                        continue;
                    }

                    multipart.add(makeChestPart(baseName, "single", facing, yRot, isEnder));
                    multipart.add(makeChestPart(baseName + "_lid", "single", facing, yRot, isEnder));

                    multipart.add(makeChestPart(baseName + "_left", "left", facing, yRot, isEnder));
                    multipart.add(makeChestPart(baseName + "_left_lid", "left", facing, yRot, isEnder));

                    multipart.add(makeChestPart(baseName + "_right", "right", facing, yRot, isEnder));
                    multipart.add(makeChestPart(baseName + "_right_lid", "right", facing, yRot, isEnder));
                }
                JsonObject root = new JsonObject();
                root.add("multipart", multipart);

                map.put("assets/minecraft/blockstates/" + name + ".json",
                        GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
            }
        }

        private static JsonObject makeChestPart(String model, String type, String facing, int yRot, boolean isEnder) {
            JsonObject when = new JsonObject();
            if (!isEnder)
                when.addProperty("type", type);
            when.addProperty("facing", facing);

            JsonObject apply = new JsonObject();
            apply.addProperty("model", "minecraft:block/" + model);
            if (yRot != 0) apply.addProperty("y", yRot);

            JsonObject part = new JsonObject();
            part.add("when", when);
            part.add("apply", apply);
            return part;
        }
    }
}
