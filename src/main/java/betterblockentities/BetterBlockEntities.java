package betterblockentities;

/* local */
import betterblockentities.gui.ConfigManager;

/* fabric */
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.api.ClientModInitializer;

/*
    TODO: add support for vanilla fabric (so the mod can run without
     sodium too)
    TODO: Add bed template(s) and support for custom bed colors in ModelGenerator
    TODO: Look into sign support (probably need quads for each glyph and pass to emitter)
    TODO: Other Block Entities...
*/

public class BetterBlockEntities implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(pluginContext -> {
            new ModelLoader().initialize(pluginContext);
        });
        ConfigManager.load();
    }
 }
