package org.wrzxas.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.DynamicRegistryListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class BiomeListSettingScreen extends DynamicRegistryListSettingScreen<Biome> {
    private static final Map<RegistryKey<Biome>, String> biomesKeyNames = new WeakHashMap<>(16);

    public BiomeListSettingScreen(GuiTheme theme, Setting<Set<RegistryKey<Biome>>> setting) {
        super(theme, "Select biomes", setting, setting.get(), RegistryKeys.BIOME);
    }

    @Override
    protected WWidget getValueWidget(RegistryKey<Biome> value) {
        return theme.label(BiomeListSettingScreen.get(value));
    }

    @Override
    protected String[] getValueNames(RegistryKey<Biome> value) {
        return new String[]{
            BiomeListSettingScreen.get(value),
            value.getValue().toString()
        };
    }

    private static String get(RegistryKey<Biome> biome) {
        return biomesKeyNames.computeIfAbsent(biome, key -> {
            String translationKey = "biome." + key.getValue().toTranslationKey();
            String translated = I18n.translate(translationKey);
            return translated.equals(translationKey) ? key.getValue().getPath() : translated;
        });
    }
}
