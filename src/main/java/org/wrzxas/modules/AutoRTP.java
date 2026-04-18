package org.wrzxas.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.wrzxas.Kopateli;
import org.wrzxas.settings.BiomeListSetting;

import java.util.Set;

public class AutoRTP extends Module {
    private int lastTp = -1;
    private boolean tp = false;

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<ListMode> listMode = sgGeneral.add(new EnumSetting.Builder<ListMode>()
        .name("list-mode")
        .description("Selection mode")
        .defaultValue(ListMode.Blacklist)
        .build()
    );

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
        super(Kopateli.CATEGORY, "auto-rtp", "Automatically send /rtp when player locate in some biomes.");
    }

    @EventHandler
    public void onEvent(TickEvent.Pre ignored) {
        if (!Utils.canUpdate()) return;

        RegistryKey<Biome> biome = mc.world.getBiome(mc.player.getBlockPos()).getKey().orElse(null);
        if (listMode.get() == ListMode.Blacklist && blacklist.get().contains(biome)) return;
        if (listMode.get() == ListMode.Whitelist && !whitelist.get().contains(biome)) return;
        if (lastTp == -1 || mc.player.age - lastTp >= 410) {
            tp = true;
            mc.player.networkHandler.sendChatCommand("rtp");
            tp = false;
            lastTp = mc.player.age;
        }
    }

    @EventHandler
    public void onEvent(PacketEvent.Send e) {
        if (!tp && e.packet instanceof CommandExecutionC2SPacket(String command) && command.equals("/rtp"))
            lastTp = mc.player.age;
    }

    public enum ListMode {
        Blacklist, Whitelist
    }
}
