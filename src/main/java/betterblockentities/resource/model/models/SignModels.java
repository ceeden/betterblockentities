package betterblockentities.resource.model.models;

import betterblockentities.resource.model.ModelGenerator;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/*
    generates blockstates(variants) jsons for each sign variant (which will create a model type of
    SimpleBlockStateModel during baking)

    generates block models for each sign variant
*/
public class SignModels extends ModelGenerator {
    private static final List<String> WOOD_TYPES = List.of(
            "oak", "spruce", "birch", "jungle", "acacia",
            "dark_oak", "mangrove", "cherry", "bamboo",
            "crimson", "warped", "pale_oak"
    );

    public static class Model {
        public static void generateSignModels(Map<String, byte[]> map) {
            generateSigns(map, "sign_template.json", false, true);
            generateSigns(map, "sign_wall_template.json", false, false);
            generateSigns(map, "sign_hanging_template.json", true, false);
            generateSigns(map, "sign_hanging_wall_template.json", true, false);
        }

        private static void generateSigns(Map<String, byte[]> map, String templateName, boolean hanging, boolean standing)
        {
            JsonObject template = loader.loadTemplate(templateName);
            if (template == null) return;
            var elements = loader.readTemplateElements(template);

            for (String wood : WOOD_TYPES) {
                String texture = "minecraft:entity/signs/" + (hanging ? "hanging/" : "") + wood;
                String baseName = wood + (hanging ? "_hanging_sign" : standing ? "_sign" : "_wall_sign");

                map.put("assets/minecraft/models/block/" + baseName + ".json",
                        GSON.toJson(makeModel("sign", texture, elements)).getBytes(StandardCharsets.UTF_8));
                map.put("assets/minecraft/models/block/" + wood + (hanging ? "_wall_hanging_sign" : "_wall_sign") + ".json",
                        GSON.toJson(makeModel("sign", texture, elements)).getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public static class BlockState {
        public static void generateSignBlockstates(Map<String, byte[]> map) {
            for (String wood : WOOD_TYPES) {
                generateStandingSignBlockstate(map, wood, false);
                generateWallSignBlockstate(map, wood, false);
                generateStandingSignBlockstate(map, wood, true);
                generateWallSignBlockstate(map, wood, true);
            }
        }

        private static void generateStandingSignBlockstate(Map<String, byte[]> map, String wood, boolean hanging) {
            String baseName = wood + (hanging ? "_hanging_sign" : "_sign");
            JsonObject variants = new JsonObject();
            for (int rot = 0; rot < 16; rot++) {
                float yRot = (rot * 22.5f) % 360f;

                JsonObject variant = new JsonObject();
                variant.addProperty("model", "minecraft:block/" + baseName);
                variant.addProperty("y", yRot); // apply Y rotation directly
                variants.add("rotation=" + rot, variant);
            }

            JsonObject root = new JsonObject();
            root.add("variants", variants);
            map.put("assets/minecraft/blockstates/" + baseName + ".json",
                    GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        }

        private static void generateWallSignBlockstate(Map<String, byte[]> map, String wood, boolean hanging) {
            String baseName = wood + (hanging ? "_wall_hanging_sign" : "_wall_sign");
            JsonObject variants = new JsonObject();

            String[] facings = {"north", "east", "south", "west"};
            int[] rotations = {0, 90, 180, 270};

            for (int i = 0; i < facings.length; i++) {
                String facing = facings[i];
                variants.add("facing=" + facing, createVariantFloat(baseName, 0, rotations[i]));
            }

            JsonObject root = new JsonObject();
            root.add("variants", variants);
            map.put("assets/minecraft/blockstates/" + baseName + ".json",
                    GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        }
    }
}
