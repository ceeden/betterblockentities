package betterblockentities.gui;

/* minecraft */
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

/*
    TODO: clean this shit up lol
*/

public class ConfigScreen extends GameOptionsScreen {
    private final ConfigHolder originalConfig;

    private SimpleOption<Boolean> masterToggle;
    private SimpleOption<Boolean>
            chestOpt,
            signOpt,
            shulkerOpt,
            bedOpt,
            bellOpt,
            potOpt,
            chestAnimOpt,
            signTextOpt,
            shulkerAnimOpt,
            bellAnimOpt,
            potAnimOpt;
    private SimpleOption<Integer> smoothness;

    public ConfigScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.translatable("Better Block Entities"));
        this.originalConfig = ConfigManager.CONFIG.copy();
    }

    @Override
    protected void addOptions() {
        if (this.body == null) return;

        masterToggle = masterToggle();
        chestOpt = optimizeChests();
        signOpt = optimizeSigns();
        shulkerOpt = optimizeShulkers();
        bedOpt = optimizeBeds();
        bellOpt = optimizeBells();
        potOpt = optimizeDecoratedPots();
        smoothness = extraRenderPasses();

        chestAnimOpt = chestsAnimations();
        signTextOpt = renderSignText();
        shulkerAnimOpt = shulkerAnimations();
        bellAnimOpt = bellAnimations();
        potAnimOpt = potAnimations();

        this.body.addSingleOptionEntry(masterToggle);
        this.body.addAll(
                chestOpt, chestAnimOpt,
                signOpt, signTextOpt,
                shulkerOpt, shulkerAnimOpt,
                bellOpt, bellAnimOpt,
                potOpt, potAnimOpt,
                bedOpt
        );
        this.body.addSingleOptionEntry(smoothness);
        updateDependentOptions(masterToggle.getValue());
    }

    private SimpleOption<Boolean> masterToggle() {
        return new SimpleOption<>(
                "Enable Optimizations",
                value -> Tooltip.of(Text.of("§7Turns the entire optimization system on or off.")),
                (text, value) -> value ? Text.of("§aON") : Text.of("§cOFF"),
                SimpleOption.BOOLEAN,
                ConfigManager.CONFIG.master_optimize,
                value -> {
                    ConfigManager.CONFIG.master_optimize = value;
                    updateDependentOptions(value);
                }
        );
    }

    private SimpleOption<Boolean> optimizeChests() {
        return new SimpleOption<>(
                "Optimize Chests",
                value -> Tooltip.of(Text.of("§7Turns off all Chest optimizations, overrides the option: §l§nChest Animations§r")),
                (text, value) -> value ? Text.of("§aON") : Text.of("§cOFF"),
                SimpleOption.BOOLEAN,
                ConfigManager.CONFIG.optimize_chests,
                v -> {
                    ConfigManager.CONFIG.optimize_chests = v;
                    setOptionActive(chestAnimOpt, v && masterToggle.getValue());
                }
        );
    }

    private SimpleOption<Boolean> chestsAnimations() {
        return booleanOption(
                "Chest Animations",
                ConfigManager.CONFIG.chest_animations,
                v -> ConfigManager.CONFIG.chest_animations = v
        );
    }

    private SimpleOption<Boolean> optimizeSigns() {
        return new SimpleOption<>(
                "Optimize Signs",
                value -> Tooltip.of(Text.of("§7Turns off all Sign optimizations, overrides the option: §l§nSign Text§r")),
                (text, value) -> value ? Text.of("§aON") : Text.of("§cOFF"),
                SimpleOption.BOOLEAN,
                ConfigManager.CONFIG.optimize_signs,
                v -> {
                    ConfigManager.CONFIG.optimize_signs = v;
                    setOptionActive(signTextOpt, v && masterToggle.getValue());
                }
        );
    }

    private SimpleOption<Boolean> renderSignText() {
        return booleanOption(
                "Sign Text",
                ConfigManager.CONFIG.render_sign_text,
                v -> ConfigManager.CONFIG.render_sign_text = v
        );
    }

    private SimpleOption<Boolean> optimizeShulkers() {
        return new SimpleOption<>(
                "Optimize Shulkers",
                value -> Tooltip.of(Text.of("§7Turns off all ShulkerBox optimizations, overrides the option: §l§nShulker Animations§r")),
                (text, value) -> value ? Text.of("§aON") : Text.of("§cOFF"),
                SimpleOption.BOOLEAN,
                ConfigManager.CONFIG.optimize_shulkers,
                v -> {
                    ConfigManager.CONFIG.optimize_shulkers = v;
                    setOptionActive(shulkerAnimOpt, v && masterToggle.getValue());
                }
        );
    }

    private SimpleOption<Boolean> shulkerAnimations() {
        return booleanOption(
                "Shulker Animations",
                ConfigManager.CONFIG.shulker_animations,
                v -> ConfigManager.CONFIG.shulker_animations = v
        );
    }

    private SimpleOption<Boolean> optimizeBeds() {
        return new SimpleOption<>(
                "Optimize Beds",
                value -> Tooltip.of(Text.of("§7Turns off all Bed optimizations")),
                (text, value) -> value ? Text.of("§aON") : Text.of("§cOFF"),
                SimpleOption.BOOLEAN,
                ConfigManager.CONFIG.optimize_beds,
                v -> {
                    ConfigManager.CONFIG.optimize_beds = v;
                    setOptionActive(masterToggle, v && masterToggle.getValue());
                }
        );
    }

    private SimpleOption<Boolean> optimizeBells() {
        return new SimpleOption<>(
                "Optimize Bells",
                value -> Tooltip.of(Text.of("§7Turns off all Bell optimizations, overrides the option: §l§nBell Animations§r")),
                (text, value) -> value ? Text.of("§aON") : Text.of("§cOFF"),
                SimpleOption.BOOLEAN,
                ConfigManager.CONFIG.optimize_bells,
                v -> {
                    ConfigManager.CONFIG.optimize_bells = v;
                    setOptionActive(bellAnimOpt, v && masterToggle.getValue());
                }
        );
    }

    private SimpleOption<Boolean> bellAnimations() {
        return booleanOption(
                "Bell Animations",
                ConfigManager.CONFIG.bell_animations,
                v -> ConfigManager.CONFIG.bell_animations = v
        );
    }

    private SimpleOption<Boolean> optimizeDecoratedPots() {
        return new SimpleOption<>(
                "Optimize Decorated Pots",
                value -> Tooltip.of(Text.of("§7Turns off all Decorated Pot optimizations, overrides the option: §l§nDecorated Pot Animations§r")),
                (text, value) -> value ? Text.of("§aON") : Text.of("§cOFF"),
                SimpleOption.BOOLEAN,
                ConfigManager.CONFIG.optimize_decoratedpots,
                v -> {
                    ConfigManager.CONFIG.optimize_decoratedpots = v;
                    setOptionActive(potAnimOpt, v && masterToggle.getValue());
                }
        );
    }

    private SimpleOption<Boolean> potAnimations() {
        return booleanOption(
                "Decorated Pot Animations",
                ConfigManager.CONFIG.pot_animations,
                v -> ConfigManager.CONFIG.pot_animations = v
        );
    }

    private SimpleOption<Integer> extraRenderPasses() {
        return new SimpleOption<>(
                "Extra Render Passes",
                value -> Tooltip.of(Text.of("§7The amount of extra render passes each optimized block entity should be rendered for after it stops animating, can help smooth out visual bugs")),
                (text, value) -> Text.of(text.getString() + ": " + value),
                new SimpleOption.ValidatingIntSliderCallbacks(0, 50),
                ConfigManager.CONFIG.smoothness_slider,
                v -> ConfigManager.CONFIG.smoothness_slider = v
        );
    }

    private SimpleOption<Boolean> booleanOption(String key, boolean initial, java.util.function.Consumer<Boolean> onChange) {
        return new SimpleOption<>(
                key,
                SimpleOption.emptyTooltip(),
                (text, value) -> value ? Text.of("§aON") : Text.of("§cOFF"),
                SimpleOption.BOOLEAN,
                initial,
                onChange
        );
    }

    private void updateDependentOptions(boolean enabled) {
        setOptionActive(chestOpt, enabled);
        setOptionActive(signOpt, enabled);
        setOptionActive(shulkerOpt, enabled);
        setOptionActive(bedOpt, enabled);
        setOptionActive(bellOpt, enabled);
        setOptionActive(potOpt, enabled);

        setOptionActive(chestAnimOpt, enabled && chestOpt.getValue());
        setOptionActive(signTextOpt, enabled && signOpt.getValue());
        setOptionActive(shulkerAnimOpt, enabled && shulkerOpt.getValue());
        setOptionActive(bellAnimOpt, enabled && bellOpt.getValue());
        setOptionActive(potAnimOpt, enabled && potOpt.getValue());

        setOptionActive(smoothness, enabled);
    }

    private void setOptionActive(SimpleOption<?> option, boolean active) {
        if (this.body == null) return;
        ClickableWidget widget = this.body.getWidgetFor(option);
        if (widget != null) widget.active = active;
    }

    @Override
    public void removed() {
        if (!ConfigManager.CONFIG.equals(originalConfig)) {
            ConfigManager.save();
            ConfigManager.refreshSupportedTypes();
            MinecraftClient.getInstance().reloadResources();
        }
    }
}
