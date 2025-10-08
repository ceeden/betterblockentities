package betterblockentities.resource;

/* gson */
import com.google.gson.*;
import net.minecraft.resource.*;

/* java/misc */
import java.io.*;
import java.util.*;

public class ResourceBuilder
{
    private static ResourcePack pack;
    private static ResourcePackProfile packProfile;

    public static byte[] buildZip() {
        PackMetadataBuilder meta = new PackMetadataBuilder();
        ModelGenerator models = new ModelGenerator();
        ResourcePackAssembler assembler = new ResourcePackAssembler();

        Map<String, byte[]> entries = new HashMap<>();
        entries.putAll(meta.createMetadataAndIcon());
        entries.putAll(models.generateAllModels());

        return assembler.assemble(entries);
    }

    public static void buildPack() {
        byte[] packData = buildZip();
        pack = new Pack("betterblockentities-generated", packData);

        ResourcePackProfile.PackFactory factory = new ResourcePackProfile.PackFactory() {
            @Override
            public ResourcePack open(ResourcePackInfo info) {
                return ResourceBuilder.getPack();
            }

            @Override
            public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                return ResourceBuilder.getPack();
            }
        };

        ResourcePackPosition pos = new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true);
        packProfile = ResourcePackProfile.create(getPack().getInfo(), factory, ResourceType.CLIENT_RESOURCES, pos);
    }

    private static ResourcePack getPack() {
        return pack;
    }

    public static ResourcePackProfile getPackProfile() {
        return packProfile;
    }
}
