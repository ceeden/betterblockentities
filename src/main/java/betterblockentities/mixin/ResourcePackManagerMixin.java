package betterblockentities.mixin;

/* local */
import betterblockentities.resource.pack.ResourceBuilder;

/* minecraft */
import net.minecraft.resource.*;

/* mixin */
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/* java/misc */
import java.util.Map;

/*
    there is probably a better way to inject the pack profile
    but this works like I want it to... might cause issues later on
    with other mods that modify resource packs though (because of the
    ResourcePackPosition passed to the pack profile
    "
*/
@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin
{
    @Inject(method = "providePackProfiles", at =
        @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void injectGeneratedPackProfiles(CallbackInfoReturnable<Map<String, ResourcePackProfile>> cir, Map<String, ResourcePackProfile> map) {
        ResourcePackProfile generated = ResourceBuilder.getPackProfile();
        if (generated != null && !map.containsKey(generated.getId())) {
            map.put(generated.getId(), generated);
        }
    }
}