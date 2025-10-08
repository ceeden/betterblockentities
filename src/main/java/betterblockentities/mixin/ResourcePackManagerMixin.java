package betterblockentities.mixin;

/* local */
import betterblockentities.resource.Pack;
import betterblockentities.resource.ResourceBuilder;

/* minecraft */
import net.minecraft.resource.*;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* java/misc */
import java.util.Map;
import java.util.TreeMap;


/*
    there is probably a better way to inject the pack profile
    but this works like I want it to
*/

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin
{
    @Inject(method = "providePackProfiles", at = @At("RETURN"), cancellable = true)
    private void injectGeneratedPackProfiles(CallbackInfoReturnable<Map<String, ResourcePackProfile>> cir) {
        ResourceBuilder builder = new ResourceBuilder();

        byte[] packData = builder.buildZip();
        Pack pack = new Pack("betterblockentities-generated", packData);

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

        Map<String, ResourcePackProfile> map = new TreeMap<>(cir.getReturnValue());
        ResourcePackProfile generatedProfile = ResourcePackProfile.create(pack.getInfo(), factory, ResourceType.CLIENT_RESOURCES, pos);
        if (generatedProfile != null) {
            map.put(generatedProfile.getId(), generatedProfile);
        }
        cir.setReturnValue(Map.copyOf(map));
    }
}