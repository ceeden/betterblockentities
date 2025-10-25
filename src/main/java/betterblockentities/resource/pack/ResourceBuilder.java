package betterblockentities.resource.pack;

/* gson */
import betterblockentities.resource.model.ModelGenerator;
import net.minecraft.resource.*;

/* java/misc */
import java.util.*;

public class ResourceBuilder
{
    /* generate the "in memory" resource pack to later be passed to our pack profile */
    public static byte[] buildZip() {
        PackMetadataBuilder meta = new PackMetadataBuilder();
        ModelGenerator models = new ModelGenerator();
        ResourcePackAssembler assembler = new ResourcePackAssembler();

        Map<String, byte[]> entries = new HashMap<>();
        entries.putAll(meta.createMetadataAndIcon());
        entries.putAll(models.generateAllModels());

        return assembler.assemble(entries);
    }

    /* builds the resource pack and its profile, should be called once on mod initialization */
    public static ResourcePackProfile buildPackProfile() {
        byte[] packData = buildZip();
        ResourcePack pack = new Pack("betterblockentities-generated", packData);

        ResourcePackProfile.PackFactory factory = new ResourcePackProfile.PackFactory() {
            @Override
            public ResourcePack open(ResourcePackInfo info) {
                return pack;
            }

            @Override
            public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                return pack;
            }
        };

        ResourcePackPosition pos = new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true);
        return ResourcePackProfile.create(pack.getInfo(), factory, ResourceType.CLIENT_RESOURCES, pos);
    }
}
