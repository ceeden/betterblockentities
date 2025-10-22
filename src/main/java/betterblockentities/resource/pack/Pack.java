package betterblockentities.resource.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Pack implements ResourcePack {
    private final String name;
    private final byte[] packData;
    private final Map<String, byte[]> entries = new HashMap<>();
    private final Map<ResourceType, Set<String>> namespaces = new EnumMap<>(ResourceType.class);

    public Pack(String name, byte[] packData) {
        this.name = name;
        this.packData = packData;
        buildCache();
    }

    private void buildCache() {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(packData))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    byte[] data = zip.readAllBytes();
                    entries.put(entry.getName(), data);

                    // Track namespaces for each type
                    for (ResourceType type : ResourceType.values()) {
                        String dir = type.getDirectory() + "/";
                        if (entry.getName().startsWith(dir)) {
                            String rest = entry.getName().substring(dir.length());
                            String namespace = rest.split("/", 2)[0];
                            namespaces.computeIfAbsent(type, t -> new HashSet<>()).add(namespace);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable InputSupplier<InputStream> openRoot(String... segments) {
        String path = String.join("/", segments);
        byte[] data = entries.get(path);
        return data == null ? null : () -> new ByteArrayInputStream(data);
    }

    @Override
    public @Nullable InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        String path = type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();
        byte[] data = entries.get(path);
        return data == null ? null : () -> new ByteArrayInputStream(data);
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        String base = type.getDirectory() + "/" + namespace + "/" + prefix;
        entries.forEach((path, data) -> {
            if (path.startsWith(base)) {
                String relative = path.substring(type.getDirectory().length() + 1 + namespace.length() + 1);
                consumer.accept(Identifier.of(namespace, relative), () -> new ByteArrayInputStream(data));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return namespaces.getOrDefault(type, Collections.emptySet());
    }

    @Override
    public @Nullable <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer) throws IOException {
        InputSupplier<InputStream> input = openRoot("pack.mcmeta");
        if (input == null) return null;
        try (InputStream stream = input.get()) {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            if (!json.has(metadataSerializer.name())) return null;
            JsonElement section = json.get(metadataSerializer.name());
            return metadataSerializer.codec().parse(JsonOps.INSTANCE, section).result().orElse(null);
        }
    }

    @Override
    public ResourcePackInfo getInfo() {
        return new ResourcePackInfo(name, Text.literal("bbe-generated"), ResourcePackSource.BUILTIN, Optional.empty());
    }

    @Override
    public void close() {}
}