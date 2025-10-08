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
        Map<String, ResourcePackProfile> map = new TreeMap<>(cir.getReturnValue());
        if (ResourceBuilder.getPackProfile() != null) {
            map.put(ResourceBuilder.getPackProfile().getId(), ResourceBuilder.getPackProfile());
        }
        cir.setReturnValue(Map.copyOf(map));
    }
}