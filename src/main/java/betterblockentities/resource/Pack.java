package betterblockentities.resource;

/* gson */
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

/* minecraft */
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/* java/misc */
import org.jetbrains.annotations.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Pack implements ResourcePack
{
    private final String name;
    private final byte[] packData;

    public Pack(String name, byte[] packData) {
        this.name = name;
        this.packData = packData;
    }

    @Override
    public @Nullable InputSupplier<InputStream> openRoot(String... segments) {
        String path = String.join("/", segments);
        return openEntry(path);
    }

    @Override
    public @Nullable InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        String path = type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();
        return openEntry(path);
    }

    private @Nullable InputSupplier<InputStream> openEntry(String path) {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(packData))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals(path)) {
                    byte[] data = zip.readAllBytes();
                    return () -> new ByteArrayInputStream(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        String base = type.getDirectory() + "/" + namespace + "/" + prefix;
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(packData))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.startsWith(base) && !entry.isDirectory()) {
                    String pathInsideNamespace =
                            name.substring(type.getDirectory().length() + 1 + namespace.length() + 1);
                    Identifier id = Identifier.of(namespace, pathInsideNamespace);
                    byte[] data = zip.readAllBytes();
                    consumer.accept(id, () -> new ByteArrayInputStream(data));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        Set<String> namespaces = new HashSet<>();
        String base = type.getDirectory() + "/";
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(packData))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.startsWith(base)) {
                    String[] parts = name.substring(base.length()).split("/", 2);
                    if (parts.length > 0) namespaces.add(parts[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return namespaces;
    }

    @Override
    public @Nullable <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer) throws IOException
    {
        InputSupplier<InputStream> input = openRoot("pack.mcmeta");
        if (input == null) return null;

        try (InputStream stream = input.get()) {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            if (!json.has(metadataSerializer.name())) {
                return null;
            }
            JsonElement section = json.get(metadataSerializer.name());
            var result = metadataSerializer.codec().parse(JsonOps.INSTANCE, section);
            return result.result().orElse(null);
        }
    }

    @Override
    public ResourcePackInfo getInfo() {
        return new ResourcePackInfo("betterblockentities-generated",
                Text.literal("Better Block Entities"),
                ResourcePackSource.BUILTIN,
                Optional.empty()
        );
    }

    @Override
    public void close() {}
}
