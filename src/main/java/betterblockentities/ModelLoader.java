package betterblockentities;

/* fabric */
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.*;

/* minecraft */
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelLoader implements ModelLoadingPlugin
{
    public static final ExtraModelKey<BlockStateModel> BELL_BODY_KEY =
            ExtraModelKey.create(() -> "BellBody");

    public static final Identifier BELL_BODY_ID =
            Identifier.of("betterblockentities", "block/bell_body");

    @Override
    public void initialize(Context ctx) {
        ctx.addModel(BELL_BODY_KEY, SimpleUnbakedExtraModel.blockStateModel(BELL_BODY_ID));
    }
}


