package betterblockentities.resource.model.models;

/* local */
import betterblockentities.resource.model.ModelGenerator;

/* gson */
import com.google.gson.JsonObject;

/* minecraft */
import net.minecraft.util.DyeColor;

/* java/misc */
import java.nio.charset.StandardCharsets;
import java.util.Map;

/*
    generates blockstates(variants) jsons for each colored bed (which will create a model type of
    SimpleBlockStateModel during baking)

    generates block models for each colored beds foot and head.

    TODO: should switch to using multipart(MultiPartBlockStateModel) instead of variants(SimpleBlockStateModel)
*/
public class BedModels extends ModelGenerator {
    public static class Model {
        public static void generateBedsHead(Map<String, byte[]> map) {
            JsonObject template = loader.loadTemplate("bed_head_template.json");
            if (template == null) return;
            var elements = loader.readTemplateElements(template);

            for (DyeColor color : DyeColor.values()) {
                String name = color.getId() + "_bed_head";
                String texture = "minecraft:entity/bed/" + color.getId();
                map.put("assets/minecraft/models/block/" + name + ".json",
                        GSON.toJson(makeModelWithParticle("bed", texture, "minecraft:block/oak_planks", elements)).getBytes(StandardCharsets.UTF_8));
            }
        }

        public static void generateBedsFoot(Map<String, byte[]> map) {
            JsonObject template = loader.loadTemplate("bed_foot_template.json");
            if (template == null) return;
            var elements = loader.readTemplateElements(template);

            for (DyeColor color : DyeColor.values()) {
                String name = color.getId() + "_bed_foot";
                String texture = "minecraft:entity/bed/" + color.getId();
                map.put("assets/minecraft/models/block/" + name + ".json",
                        GSON.toJson(makeModelWithParticle("bed", texture,"minecraft:block/oak_planks", elements)).getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public static class BlockState {
        public static void generateBedBlockstates(Map<String, byte[]> map) {
            for (DyeColor color : DyeColor.values()) {
                String name = color.getId() + "_bed";
                JsonObject variants = new JsonObject();

                String[] facings = {"north", "south", "west", "east"};
                String[] parts = {"head", "foot"};
                boolean[] occupiedValues = {false, true};

                for (String facing : facings) {
                    for (String part : parts) {
                        for (boolean occupied : occupiedValues) {
                            String key = String.format("facing=%s,part=%s,occupied=%s", facing, part, occupied);
                            int yRot = switch (facing) {
                                case "south" -> 180;
                                case "west" -> 270;
                                case "east" -> 90;
                                default -> 0;
                            };

                            String model = "minecraft:block/" + name + "_" + part;

                            JsonObject variant = new JsonObject();
                            variant.addProperty("model", model);
                            variant.addProperty("y", yRot);

                            variants.add(key, variant);
                        }
                    }
                }
                JsonObject root = new JsonObject();
                root.add("variants", variants);
                map.put("assets/minecraft/blockstates/" + name + ".json",
                        GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
