package betterblockentities;

/* local */
import betterblockentities.gui.ConfigManager;

/* fabric */
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.api.ClientModInitializer;

/*
    TODO: Add support for vanilla fabric (so the mod can run without
     sodium too)
    TODO: Improve signs, make them completely baked into meshes (sign text)
    TODO: Add support for pottery patterns (decorated pots)
    TODO: Check sign ticker logic and implement same approach from the other BE´s
          if possible
    TODO: Other Block Entities...
*/

public class BetterBlockEntities implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        /* register our model loader */
        ModelLoadingPlugin.register(pluginContext -> { new ModelLoader().initialize(pluginContext); });

        /* load config from disk file */
        ConfigManager.load();

        /* updates the list of supported block entity types and cached config in BlockEntityManager */
        ConfigManager.refreshSupportedTypes();
    }
 }
