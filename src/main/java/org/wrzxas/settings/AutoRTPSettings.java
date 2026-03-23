package org.wrzxas.settings;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class AutoRTPSettings {
    private final Map<Class<?>, SettingsWidgetFactory.Factory> factories;

    private final GuiTheme theme;

    public AutoRTPSettings(Map<Class<?>, SettingsWidgetFactory.Factory> factories, GuiTheme theme) {
        this.factories = factories;
        this.theme = theme;
    }

    public void addSettings() {
        factories.put(BiomeListSetting.class, (table, setting) -> biomeListW(table, (BiomeListSetting) setting));
    }

    private void biomeListW(WTable table, BiomeListSetting setting) {
        WButton button = table.add(theme.button("Select")).expandCellX().widget();
        button.action = () -> mc.setScreen(new BiomeListSettingScreen(theme, setting));

        WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
        reset.action = setting::reset;
    }
}
