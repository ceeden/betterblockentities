package betterblockentities.resource;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TemplateLoader
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public JsonObject loadTemplate(String template) {
        try (InputStream input = TemplateLoader.class.getResourceAsStream(
                "/assets/betterblockentities/models/block/" + template)) {
            if (input == null) return null;
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ElementRecord> readTemplateElements(JsonObject templateRoot) {
        List<ElementRecord> elements = new ArrayList<>();
        JsonArray arr = templateRoot.getAsJsonArray("elements");

        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            List<Float> from = jsonArrayToFloatList(obj.getAsJsonArray("from"));
            List<Float> to = jsonArrayToFloatList(obj.getAsJsonArray("to"));
            Map<String, FaceRecord> faces = readFaces(obj.getAsJsonObject("faces"));
            elements.add(new ElementRecord(from, to, faces));
        }
        return elements;
    }

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
        for (JsonElement e : arr) list.add(e.getAsFloat());
        return list;
    }

    public record ElementRecord(List<Float> from, List<Float> to, Map<String, FaceRecord> faces) {}
    public record FaceRecord(List<Float> uv, String texture, Float rotation) {}
}
