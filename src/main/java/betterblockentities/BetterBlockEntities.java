package betterblockentities;

/* local */
import betterblockentities.gui.ConfigManager;

/* fabric */
import betterblockentities.resource.pack.ResourceBuilder;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.api.ClientModInitializer;

/*
    TODO: Add support for vanilla fabric (so the mod can run without
     sodium too)
    TODO: Improve signs, make them completely baked into meshes (sign text)
    TODO: Add support for pottery patterns (decorated pots)
    TODO: Other Block Entities...
*/

public class BetterBlockEntities implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        ResourceBuilder.buildPack();

        ModelLoadingPlugin.register(pluginContext -> {
            new ModelLoader().initialize(pluginContext);
        });
        ConfigManager.load();
    }
 }
