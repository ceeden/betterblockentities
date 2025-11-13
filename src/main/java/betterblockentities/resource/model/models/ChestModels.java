package betterblockentities.resource.model.models;

/* local */
import betterblockentities.resource.model.ModelGenerator;

/* gson */
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.DyeColor;

/* java/misc */
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
            "chest", "trapped_chest", "ender_chest"
    );

    public static class Model {
        /* this is super ugly ik */
        private static String getParticleTexture(String textureName) {
            if (textureName.contains("ender")) {
                return "minecraft:block/obsidian";
            } else if (textureName.contains("exposed")) {
                return "minecraft:block/exposed_copper";
            } else if (textureName.contains("oxidized")) {
                return "minecraft:block/oxidized_copper";
            } else if (textureName.contains("weathered")) {
                return "minecraft:block/weathered_copper";
            } else if (textureName.contains("copper")) {
                return "minecraft:block/copper_block";
            } else {
                return "minecraft:block/oak_planks";
            }
        }

        public static void generateLeftChests(Map<String, byte[]> map) {
            generateChestSide(map, "left_chest_template.json", "_left", Map.of(
                    "chest_left_lid", "normal_left",
                    "trapped_chest_left_lid", "trapped_left"
            ));

            generateChestSide(map, "left_chest_lid_template.json", "_left", Map.of(
                    "chest_left", "normal_left",
                    "trapped_chest_left", "trapped_left"
            ));
        }

        public static void generateRightChests(Map<String, byte[]> map) {
            generateChestSide(map, "right_chest_template.json", "_right", Map.of(
                    "chest_right", "normal_right",
                    "trapped_chest_right", "trapped_right"
            ));

            generateChestSide(map, "right_chest_lid_template.json", "_right", Map.of(
                    "chest_right_lid", "normal_right",
                    "trapped_chest_right_lid", "trapped_right"
            ));
        }

        public static void generateSingleChests(Map<String, byte[]> map) {
            generateChestSide(map, "chest_template.json", "_single", Map.of(
                    "chest", "normal",
                    "trapped_chest", "trapped",
                    "ender_chest", "ender"
            ));

            generateChestSide(map, "chest_lid_template.json", "_single", Map.of(
                    "chest_lid", "normal",
                    "trapped_chest_lid", "trapped",
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
                        GSON.toJson(makeModelWithParticle("chest", texture, getParticleTexture(texture), elements)).getBytes(StandardCharsets.UTF_8));
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
