package betterblockentities.helpers;

import betterblockentities.resource.InMemoryResourceBuilder;
import net.minecraft.resource.*;
import net.minecraft.text.Text;

import java.io.File;
import java.util.Optional;

public class InMemoryResourcePackBuilder
{
    public static ResourcePackProfile create()
    {
        InMemoryResourceBuilder builder = new InMemoryResourceBuilder();
        builder.generateShulkerBoxes();
        builder.generateLeftChests();
        builder.generateRightChests();
        builder.generateShulkerBlockstates();
        builder.generateChestBlockstates();

        byte[] packData = builder.buildZip();
        Pack inMemoryPack = new Pack("betterblockentities-generated", packData);

        ResourcePackProfile.PackFactory factory = new ResourcePackProfile.PackFactory()
        {
            @Override
            public ResourcePack open(ResourcePackInfo info) {
                return inMemoryPack;
            }

            @Override
            public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                return inMemoryPack;
            }
        };

        ResourcePackInfo info = new ResourcePackInfo(
                "betterblockentities-generated",
                Text.literal("Better-Block-Entities"),
                ResourcePackSource.BUILTIN,
                Optional.empty()
        );

        ResourcePackPosition pos = new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true);

        return ResourcePackProfile.create(info, factory, ResourceType.CLIENT_RESOURCES, pos);
    }
}
