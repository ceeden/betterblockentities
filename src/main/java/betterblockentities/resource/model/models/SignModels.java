package betterblockentities.resource.model.models;

/* local */
import betterblockentities.resource.model.ModelGenerator;

/* gson */
import com.google.gson.JsonObject;
import net.minecraft.util.DyeColor;

/* java/misc */
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SignModels extends ModelGenerator {
    private static final List<String> WOOD_TYPES = List.of(
            "oak", "spruce", "birch", "jungle", "acacia",
            "dark_oak", "mangrove", "cherry", "bamboo",
            "crimson", "warped", "pale_oak"
    );

    public static class Model {
        private static String getParticleTexture(String woodtype) {
            return "minecraft:block/" + woodtype + "_planks";
        }

        public static void generateSignModels(Map<String, byte[]> map) {
            generateSigns(map, "sign_template.json", false, true);            // Standing signs
            generateSigns(map, "sign_wall_template.json", false, false);      // Wall signs
            generateSigns(map, "sign_hanging_template.json", true, false);    // Ceiling hanging signs
            generateSigns(map, "sign_hanging_wall_template.json", true, true);// Wall hanging signs
            generateAttachedHangingSigns(map); // Ceiling hanging signs (attached)
        }

        private static void generateSigns(Map<String, byte[]> map, String templateName, boolean hanging, boolean standing) {
            JsonObject template = loader.loadTemplate(templateName);
            if (template == null) return;
            var elements = loader.readTemplateElements(template);
            for (String wood : WOOD_TYPES) {
                String texture = "minecraft:entity/signs/" + (hanging ? "hanging/" : "") + wood;
                String modelName;
                if (hanging && templateName.contains("wall")) {
                    modelName = wood + "_wall_hanging_sign";
                } else if (hanging) {
                    modelName = wood + "_hanging_sign";
                } else if (!standing) {
                    modelName = wood + "_wall_sign";
                } else {
                    modelName = wood + "_sign";
                }
                map.put("assets/minecraft/models/block/" + modelName + ".json",
                        GSON.toJson(makeModelWithParticle("sign", texture, getParticleTexture(wood), elements)).getBytes(StandardCharsets.UTF_8));
            }
        }

        private static void generateAttachedHangingSigns(Map<String, byte[]> map) {
            JsonObject template = loader.loadTemplate("sign_hanging_attached_template.json");
            if (template == null) return;
            var elements = loader.readTemplateElements(template);

            for (String wood : WOOD_TYPES) {
                String texture = "minecraft:entity/signs/hanging/" + wood;
                String modelName = wood + "_hanging_sign_attached";

                map.put("assets/minecraft/models/block/" + modelName + ".json",
                        GSON.toJson(makeModelWithParticle("sign", texture, getParticleTexture(wood), elements)).getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public static class BlockState {
        public static void generateSignBlockstates(Map<String, byte[]> map) {
            for (String wood : WOOD_TYPES) {
                generateBlockstate(map, wood, false, true);   // Standing signs
                generateBlockstate(map, wood, false, false);  // Wall signs
                generateBlockstate(map, wood, true, false);   // Ceiling hanging signs (with attached property)
                generateBlockstate(map, wood, true, true);    // Wall hanging signs
            }
        }

        private static void generateBlockstate(Map<String, byte[]> map, String wood, boolean hanging, boolean wall) {
            String baseName;
            if (hanging && wall) {
                baseName = wood + "_wall_hanging_sign";
            } else if (hanging) {
                baseName = wood + "_hanging_sign";
            } else if (wall) {
                baseName = wood + "_wall_sign";
            } else {
                baseName = wood + "_sign";
            }

            JsonObject variants = new JsonObject();

            if (hanging && !wall) {
                for (int rot = 0; rot < 16; rot++) {
                    JsonObject detachedVariant = new JsonObject();
                    detachedVariant.addProperty("model", "minecraft:block/" + baseName);
                    detachedVariant.addProperty("rotation", rot);
                    variants.add("attached=false,rotation=" + rot, detachedVariant);

                    JsonObject attachedVariant = new JsonObject();
                    attachedVariant.addProperty("model", "minecraft:block/" + baseName + "_attached");
                    attachedVariant.addProperty("rotation", rot);
                    variants.add("attached=true,rotation=" + rot, attachedVariant);
                }
            } else if (wall) {
                String[] facings = {"north", "east", "south", "west"};
                int[] rotations = {0, 90, 180, 270};
                for (int i = 0; i < facings.length; i++) {
                    variants.add("facing=" + facings[i], createVariant(baseName, 0, rotations[i]));
                }
            } else {
                for (int rot = 0; rot < 16; rot++) {
                    JsonObject variant = new JsonObject();
                    variant.addProperty("model", "minecraft:block/" + baseName);
                    variants.add("rotation=" + rot, variant);
                }
            }

            JsonObject root = new JsonObject();
            root.add("variants", variants);

            map.put("assets/minecraft/blockstates/" + baseName + ".json",
                    GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
        }
    }
}
