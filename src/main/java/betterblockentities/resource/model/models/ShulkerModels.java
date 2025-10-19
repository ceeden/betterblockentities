package betterblockentities.resource.model.models;

import betterblockentities.resource.model.ModelGenerator;
import com.google.gson.JsonObject;
import net.minecraft.util.DyeColor;
import com.google.gson.JsonArray;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/*
    generates blockstates(multipart) jsons for each colored shulker (which will create a model type of
    MultiPartBlockStateModel during baking)

    generates block models for each colored shulker (base, lid)
*/
public class ShulkerModels extends ModelGenerator {
    public static class Model {
        public static void generateShulkerBase(Map<String, byte[]> map) {
            JsonObject template = loader.loadTemplate("shulker_base_template.json");
            if (template == null) return;
            var elements = loader.readTemplateElements(template);

            for (DyeColor color : DyeColor.values()) {
                String name = color.getId() + "_shulker_box_base";
                String texture = "minecraft:entity/shulker/shulker_" + color.getId();
                map.put("assets/minecraft/models/block/" + name + ".json",
                        GSON.toJson(makeModel("shulker", texture, elements)).getBytes(StandardCharsets.UTF_8));
            }

            String baseName = "shulker_box_base";
            String baseTexture = "minecraft:entity/shulker/shulker";
            map.put("assets/minecraft/models/block/" + baseName + ".json",
                    GSON.toJson(makeModel("shulker", baseTexture, elements)).getBytes(StandardCharsets.UTF_8));
        }

        public static void generateShulkerLid(Map<String, byte[]> map) {
            JsonObject template = loader.loadTemplate("shulker_lid_template.json");
            if (template == null) return;
            var elements = loader.readTemplateElements(template);

            for (DyeColor color : DyeColor.values()) {
                String name = color.getId() + "_shulker_box_lid";
                String texture = "minecraft:entity/shulker/shulker_" + color.getId();
                map.put("assets/minecraft/models/block/" + name + ".json",
                        GSON.toJson(makeModel("shulker", texture, elements)).getBytes(StandardCharsets.UTF_8));
            }

            String baseName = "shulker_box_lid";
            String baseTexture = "minecraft:entity/shulker/shulker";
            map.put("assets/minecraft/models/block/" + baseName + ".json",
                    GSON.toJson(makeModel("shulker", baseTexture, elements)).getBytes(StandardCharsets.UTF_8));
        }
    }

    public static class BlockState {
        public static void generateShulkerBlockstates(Map<String, byte[]> map) {
            Map<String, int[]> facingRot = Map.of(
                    "up", new int[]{0, 180},
                    "down", new int[]{180, 180},
                    "north", new int[]{90, 0},
                    "south", new int[]{90, 180},
                    "west", new int[]{90, 270},
                    "east", new int[]{90, 90}
            );

            for (DyeColor color : DyeColor.values()) {
                String baseName = color.getId() + "_shulker_box";
                JsonArray multipart = new JsonArray();

                for (var entry : facingRot.entrySet()) {
                    String facing = entry.getKey();
                    int xRot = entry.getValue()[0];
                    int yRot = entry.getValue()[1];

                    multipart.add(makeShulkerPart(baseName + "_base", facing, xRot, yRot));
                    multipart.add(makeShulkerPart(baseName + "_lid", facing, xRot, yRot));
                }

                JsonObject root = new JsonObject();
                root.add("multipart", multipart);
                map.put("assets/minecraft/blockstates/" + baseName + ".json",
                        GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
            }

            String baseName = "shulker_box";
            JsonArray baseMultipart = new JsonArray();

            for (var entry : facingRot.entrySet()) {
                String facing = entry.getKey();
                int xRot = entry.getValue()[0];
                int yRot = entry.getValue()[1];

                baseMultipart.add(makeShulkerPart(baseName + "_base", facing, xRot, yRot));
                baseMultipart.add(makeShulkerPart(baseName + "_lid", facing, xRot, yRot));
            }

            JsonObject baseRoot = new JsonObject();
            baseRoot.add("multipart", baseMultipart);
            map.put("assets/minecraft/blockstates/" + baseName + ".json",
                    GSON.toJson(baseRoot).getBytes(StandardCharsets.UTF_8));
        }

        private static JsonObject makeShulkerPart(String model, String facing, int xRot, int yRot) {
            JsonObject when = new JsonObject();
            when.addProperty("facing", facing);

            JsonObject apply = new JsonObject();
            apply.addProperty("model", "minecraft:block/" + model);
            if (xRot != 0) apply.addProperty("x", xRot);
            if (yRot != 0) apply.addProperty("y", yRot);

            JsonObject part = new JsonObject();
            part.add("when", when);
            part.add("apply", apply);
            return part;
        }
    }
}
