package betterblockentities.resource;

/* gson */
import com.google.gson.*;

/* java/misc */
import java.io.*;
import java.util.*;

public class ResourceBuilder
{
    public byte[] buildZip() {
        PackMetadataBuilder meta = new PackMetadataBuilder();
        ModelGenerator models = new ModelGenerator();
        ResourcePackAssembler assembler = new ResourcePackAssembler();

        Map<String, byte[]> entries = new HashMap<>();
        entries.putAll(meta.createMetadataAndIcon());
        entries.putAll(models.generateAllModels());

        return assembler.assemble(entries);
    }
}
