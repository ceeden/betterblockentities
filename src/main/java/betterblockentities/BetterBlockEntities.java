package betterblockentities;

/* local */
import betterblockentities.gui.ConfigManager;

/* fabric */
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.api.ClientModInitializer;

/*
    TODO: add support for vanilla fabric (so the mod can run without
     sodium too)
    TODO: add support for nvidium https://github.com/MCRcortex/nvidium
    TODO: fix double-chest UV mappings for left and right (make them look
     nicer).
    TODO:
     fix bug where chests don't animate on some world-saves. weirdly enough
     if you replace the chest it starts animating again
*/

public class BetterBlockEntities implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModelLoadingPlugin.register(pluginContext -> {
            new ModelLoader().initialize(pluginContext);
        });

        ConfigManager.load();
    }
 }
