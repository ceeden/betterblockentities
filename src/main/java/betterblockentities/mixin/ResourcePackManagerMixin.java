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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/* java/misc */
import java.util.Map;


/*
    there is probably a better way to inject the pack profile
    but this works like I want it to
*/

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin
{
    /*
    @Shadow @Final private Set<ResourcePackProvider> providers;

    @Inject(method = "providePackProfiles", at = @At("RETURN"), cancellable = true)
    private void injectGeneratedPackProfiles(CallbackInfoReturnable<Map<String, ResourcePackProfile>> cir) {
        Map<String, ResourcePackProfile> map = new TreeMap<>(cir.getReturnValue());

        if (ResourceBuilder.getPackProfile() != null) {
            map.put(ResourceBuilder.getPackProfile().getId(), ResourceBuilder.getPackProfile());
        }

        cir.setReturnValue(Map.copyOf(map));
    }
     */

    @Inject(
            method = "providePackProfiles",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void injectGeneratedPackProfiles(CallbackInfoReturnable<Map<String, ResourcePackProfile>> cir, Map<String, ResourcePackProfile> map) {
        ResourcePackProfile generated = ResourceBuilder.getPackProfile();
        if (generated != null && !map.containsKey(generated.getId())) {
            map.put(generated.getId(), generated);
        }
    }
}
