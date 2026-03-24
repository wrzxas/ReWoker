package org.wrzxas.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.wrzxas.AutoRTPAddon;
import org.wrzxas.settings.BiomeListSetting;

import java.util.Set;

public class AutoRTP extends Module {
    private int timer = 0;

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();


    private final Setting<ListMode> listMode = sgGeneral.add(new EnumSetting.Builder<ListMode>()
        .name("list-mode")
        .description("Selection mode")
        .defaultValue(ListMode.Blacklist)
        .build());

    private final Setting<Set<RegistryKey<Biome>>> blacklist = sgGeneral.add(new BiomeListSetting.Builder()
        .name("blacklist")
        .description("The biomes not to leave from.")
        .visible(() -> listMode.get() == ListMode.Blacklist)
        .build()
    );

    private final Setting<Set<RegistryKey<Biome>>> whitelist = sgGeneral.add(new BiomeListSetting.Builder()
        .name("whitelist")
        .description("The biomes to leave from.")
        .visible(() -> listMode.get() == ListMode.Whitelist)
        .build()
    );

    public AutoRTP() {
        super(AutoRTPAddon.CATEGORY, "auto-rtp", "Automatically send /rtp when player locate in some biomes.");
    }

    @EventHandler
    public void onEvent(TickEvent.Pre ignored) {
        if (!Utils.canUpdate()) return;
        timer++;
        RegistryKey<Biome> biome = mc.world.getBiome(mc.player.getBlockPos()).getKey().orElse(null);
        if (listMode.get() == ListMode.Blacklist && blacklist.get().contains(biome)) return;
        if (listMode.get() == ListMode.Whitelist && !whitelist.get().contains(biome)) return;
        if (timer >= 410) {
            mc.player.networkHandler.sendChatCommand("rtp");
            timer = 0;
        }
    }

    public enum ListMode {
        Blacklist, Whitelist
    }
}
