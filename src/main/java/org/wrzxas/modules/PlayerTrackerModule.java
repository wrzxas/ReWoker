package org.wrzxas.modules;

import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.MeteorToast;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import org.wrzxas.ReWoker;
import org.wrzxas.systems.PlayerTracker;

import java.util.*;

public class PlayerTrackerModule extends Module {
    private final Map<UUID, String> names = new HashMap<>();

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Mode> notificationMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("notification-mode")
        .description("The mode to use for notifications.")
        .defaultValue(Mode.Chat)
        .build()
    );

    private final Setting<Boolean> sound = sgGeneral.add(new BoolSetting.Builder()
        .name("sound")
        .description("Play sound when notify.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> joinedServer = sgGeneral.add(new BoolSetting.Builder()
        .name("joined-server")
        .description("Notify when tracked player joined server.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> leftServer = sgGeneral.add(new BoolSetting.Builder()
        .name("left-server")
        .description("Notify when tracked player left server.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> enterNearby = sgGeneral.add(new BoolSetting.Builder()
        .name("enter-nearby")
        .description("Notify when tracked player entered visual range.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> leftNearby = sgGeneral.add(new BoolSetting.Builder()
        .name("leave-nearby")
        .description("Notify when tracked player left visual range.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> showCoordinates = sgGeneral.add(new BoolSetting.Builder()
        .name("show-coordinates")
        .description("Add coordinates of player which left visual range.")
        .defaultValue(false)
        .visible(leftNearby::get)
        .build()
    );

    public PlayerTrackerModule() {
        super(ReWoker.CATEGORY, "player-tracker", "Tracks selected players activity.");
    }

    @Override
    public void onActivate() {
        names.clear();
        if (mc.getNetworkHandler() == null) return;
        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            GameProfile profile = entry.getProfile();
            names.put(profile.id(), profile.name());
        }
    }

    @EventHandler
    public void onEvent(PacketEvent.Receive e) {
        Packet<?> packet = e.packet;
        PlayerTracker playerTracker = PlayerTracker.get();
        if (packet instanceof PlayerListS2CPacket p && joinedServer.get()) {
            if (!p.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) return;
            for (PlayerListS2CPacket.Entry entry : p.getPlayerAdditionEntries()) {
                String name = entry.profile().name();
                if (name.equals(mc.player.getName().getString())) continue;
                if (playerTracker.get(name) == null) continue;
                notify("Player (highlight)%s(default) joined server.", true, name);
                names.put(entry.profileId(), name);
            }
        }

        if (packet instanceof PlayerRemoveS2CPacket(List<UUID> profileIds) && leftServer.get()) {
            for (UUID uuid : profileIds) {
                String name = names.remove(uuid);
                if (name == null) continue;
                notify("Player (highlight)%s(default) left server.", false, name);
            }
        }
    }

    @EventHandler
    public void onEvent(EntityAddedEvent e) {
        if (!enterNearby.get()) return;
        if (!(e.entity instanceof PlayerEntity player)) return;
        if (PlayerTracker.get().get(player) != null)
            notify("Player (highlight)%s(default) entered visual range.", true, player.getName().getString());
    }

    @EventHandler
    public void onEvent(EntityRemovedEvent e) {
        if (!leftNearby.get()) return;
        if (!(e.entity instanceof PlayerEntity player)) return;
        if (PlayerTracker.get().get(player) == null) return;
        String msg = "Player (highlight)%s(default) left visual range";
        if (showCoordinates.get()) msg += ChatUtils.formatCoords(player.getEntityPos());
        notify(msg + ".", false, player.getName().getString());
    }

    private void notify(String msg, boolean entered, Object... args) {
        Mode mode = notificationMode.get();
        if (mode != Mode.Toast) ChatUtils.sendMsg(Formatting.GRAY, msg, args);
        if (mode != Mode.Chat) {
            MeteorToast toast = new MeteorToast.Builder(title)
                .icon(Items.PLAYER_HEAD)
                .text(String.format(msg, args))
                .build();
            mc.getToastManager().add(toast);
        }
        if (sound.get()) {
            SoundEvent sound = entered ? SoundEvents.ENTITY_PLAYER_LEVELUP : SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
            mc.world.playSound(mc.player, mc.player.getBlockPos(), sound, SoundCategory.BLOCKS, 1f, 1f);
        }
    }

    public enum Mode {
        Chat, Toast, Both
    }
}
