package betterblockentities.resource;

/* gson */
import com.google.gson.*;

/* java/misc */
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TemplateLoader {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    /* loads a model template from the mod resources */
    public JsonObject loadTemplate(String template) {
        try (InputStream input = TemplateLoader.class.getResourceAsStream(
                "/assets/betterblockentities/models/block/templates/" + template)) {
            if (input == null) return null;
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* reads the "elements" array from a model template */
    public List<ElementRecord> readTemplateElements(JsonObject templateRoot) {
        List<ElementRecord> elements = new ArrayList<>();
        JsonArray arr = templateRoot.getAsJsonArray("elements");
        if (arr == null) return elements;

        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();

            List<Float> from = jsonArrayToFloatList(obj.getAsJsonArray("from"));
            List<Float> to = jsonArrayToFloatList(obj.getAsJsonArray("to"));
            Map<String, FaceRecord> faces = readFaces(obj.getAsJsonObject("faces"));

            RotationRecord rotation = null;
            if (obj.has("rotation")) {
                JsonObject rotObj = obj.getAsJsonObject("rotation");
                List<Float> origin = rotObj.has("origin") ? jsonArrayToFloatList(rotObj.getAsJsonArray("origin")) : List.of(8f, 8f, 8f);
                String axis = rotObj.has("axis") ? rotObj.get("axis").getAsString() : "y";
                float angle = rotObj.has("angle") ? rotObj.get("angle").getAsFloat() : 0f;
                boolean rescale = rotObj.has("rescale") && rotObj.get("rescale").getAsBoolean();
                rotation = new RotationRecord(origin, axis, angle, rescale);
            }

            elements.add(new ElementRecord(from, to, faces, rotation));
        }
        return elements;
    }

    /* reads the "faces" object from an element */
    private Map<String, FaceRecord> readFaces(JsonObject faces) {
        Map<String, FaceRecord> map = new HashMap<>();
        for (String face : faces.keySet()) {
            JsonObject faceData = faces.getAsJsonObject(face);
            List<Float> uv = faceData.has("uv") ? jsonArrayToFloatList(faceData.getAsJsonArray("uv")) : List.of();
            String texture = faceData.has("texture") ? faceData.get("texture").getAsString() : "";
            Float rotation = faceData.has("rotation") ? faceData.get("rotation").getAsFloat() : null;
            map.put(face, new FaceRecord(uv, texture, rotation));
        }
        return map;
    }

    private List<Float> jsonArrayToFloatList(JsonArray arr) {
        List<Float> list = new ArrayList<>();
        if (arr == null) return list;
        for (JsonElement e : arr) list.add(e.getAsFloat());
        return list;
    }

    public record ElementRecord(List<Float> from, List<Float> to, Map<String, FaceRecord> faces, RotationRecord rotation) {}
    public record FaceRecord(List<Float> uv, String texture, Float rotation) {}
    public record RotationRecord(List<Float> origin, String axis, float angle, boolean rescale) {}
}
