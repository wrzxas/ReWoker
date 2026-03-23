package org.wrzxas.settings;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.base.DynamicRegistryListSettingScreen;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.StringHelper;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public class BiomeListSettingScreen extends DynamicRegistryListSettingScreen<Biome> {
    private static final Map<RegistryKey<Biome>, String> biomesKeyNames = new WeakHashMap<>(16);
    private static final Map<RegistryEntry<Biome>, String> biomesEntryNames = new Reference2ObjectOpenHashMap<>(16);

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

    @SuppressWarnings("StringEquality")
    private static String get(RegistryKey<Biome> biome) {
        return biomesKeyNames.computeIfAbsent(biome, biome1 -> Optional.ofNullable(MinecraftClient.getInstance().getNetworkHandler())
            .map(ClientPlayNetworkHandler::getRegistryManager)
            .flatMap(registryManager -> registryManager.getOptional(RegistryKeys.BIOME))
            .flatMap(registry -> registry.getEntry(biome.getValue()))
            .map(BiomeListSettingScreen::get)
            .orElseGet(() -> {
                String key = "biome." + biome1.getValue().toTranslationKey();
                String translated = I18n.translate(key);
                return translated == key ? biome1.getValue().toString() : translated;
            }));
    }

    private static String get(RegistryEntry<Biome> biome) {
        return biomesEntryNames.computeIfAbsent(biome, ignored -> StringHelper.stripTextFormat(biome.value().toString()));
    }
}
