package betterblockentities.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;


public class ConfigScreen extends GameOptionsScreen {

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("Better Block Entities"));
    }

    @Override
    protected void addOptions() {
        if (this.body != null) {
            this.body.addAll(
                    UseAnimations(),
                    SmoothnessSlider()
            );
        }
    }

    private SimpleOption<Boolean> UseAnimations()
    {
        return new SimpleOption<>(
                "Animations",
                SimpleOption.emptyTooltip(),
                (text, value) -> value
                        ? Text.of("ON")
                        : Text.of("OFF"),
                SimpleOption.BOOLEAN,
                ConfigManager.CONFIG.use_animations,
                value -> ConfigManager.CONFIG.use_animations = value
        );
    }

    private SimpleOption<Integer> SmoothnessSlider()
    {
        return new SimpleOption<>(
                "Smoothness",
                SimpleOption.emptyTooltip(),
                (text, value) -> Text.of(text.getString() + ": " + value),
                new SimpleOption.ValidatingIntSliderCallbacks(0, 20),
                ConfigManager.CONFIG.smoothness_slider,
                value -> ConfigManager.CONFIG.smoothness_slider = value
        );
    }

    @Override
    public void removed() {
        // Save config when closing the screen
        ConfigManager.save();
    }
}
