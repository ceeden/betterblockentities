package betterblockentities.gui;

/* modmenu */
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;


/* TODO: add a config screen */
public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> new ConfigScreen(parent);
    }
}
