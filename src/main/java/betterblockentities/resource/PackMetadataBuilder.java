package betterblockentities.resource;

import com.google.gson.*;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.PackVersion;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

public class PackMetadataBuilder
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public Map<String, byte[]> createMetadataAndIcon() {
        Map<String, byte[]> entries = new HashMap<>();

        entries.put("pack.mcmeta", createPackMcmeta());
        loadIcon(entries);

        return entries;
    }

    private byte[] createPackMcmeta() {
        PackVersion version = SharedConstants.getGameVersion().packVersion(ResourceType.CLIENT_RESOURCES);
        int versionNmr = version.major();

        JsonObject packJson = new JsonObject();
        JsonObject packInfo = new JsonObject();
        packInfo.add("min_format", GSON.toJsonTree(new int[]{versionNmr, 0}));
        packInfo.add("max_format", GSON.toJsonTree(new int[]{versionNmr, 0}));
        packInfo.addProperty("description", "BBE Resources");
        packJson.add("pack", packInfo);

        return GSON.toJson(packJson).getBytes(StandardCharsets.UTF_8);
    }

    private void loadIcon(Map<String, byte[]> entries) {
        try (InputStream input = PackMetadataBuilder.class.getResourceAsStream(
                "/assets/betterblockentities/icon.png")) {
            if (input != null) {
                entries.put("pack.png", input.readAllBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
