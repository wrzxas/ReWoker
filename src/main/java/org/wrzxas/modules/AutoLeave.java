package org.wrzxas.modules;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import org.wrzxas.Kopateli;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.block.Block;

import java.util.List;

public class AutoLeave extends Module {
    private long sendTimer = -1;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup leaveIf = settings.createGroup("leave-if");

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-disable")
        .description("Disable the module after executing an action.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> players = leaveIf.add(new BoolSetting.Builder()
        .name("player-nearby")
        .description("Triggers when a player is within range.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> vPlayers = leaveIf.add(new IntSetting.Builder()
        .name("player-distance")
        .description("Maximum distance to detect nearby players.")
        .defaultValue(50)
        .range(5, 300)
        .sliderRange(5, 300)
        .visible(players::get)
        .build()
    );

    private final Setting<Action> aPlayers = leaveIf.add(new EnumSetting.Builder<Action>()
        .name("player-action")
        .description("Action to perform when a nearby player is detected.")
        .defaultValue(Action.Hub)
        .visible(players::get)
        .build()
    );

    private final Setting<String> cPlayers = leaveIf.add(new StringSetting.Builder()
        .name("player-message")
        .description("Message to send when player action is set to chat.")
        .defaultValue("")
        .visible(() -> players.get() && aPlayers.get() == Action.Chat)
        .build()
    );

    private final Setting<Boolean> health = leaveIf.add(new BoolSetting.Builder()
        .name("low-hp")
        .description("Triggers when your health is below the specified value.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> vHealth = leaveIf.add(new DoubleSetting.Builder()
        .name("hp")
        .description("Health threshold for triggering the action.")
        .defaultValue(4)
        .range(0, 20)
        .sliderRange(0, 20)
        .visible(health::get)
        .build()
    );

    private final Setting<Action> aHealth = leaveIf.add(new EnumSetting.Builder<Action>()
        .name("health-action")
        .description("Action to perform when your health is low.")
        .defaultValue(Action.Hub)
        .visible(health::get)
        .build()
    );

    private final Setting<String> cHealth = leaveIf.add(new StringSetting.Builder()
        .name("health-message")
        .description("Message to send when health action is set to chat.")
        .defaultValue("")
        .visible(() -> health.get() && aHealth.get() == Action.Chat)
        .build()
    );

    private final Setting<Boolean> blocks = leaveIf.add(new BoolSetting.Builder()
        .name("in-block")
        .description("Triggers when you are standing inside one of the selected blocks.")
        .defaultValue(false)
        .build()
    );

    private final Setting<List<Block>> vBlocks = leaveIf.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("Blocks that will trigger the action.")
        .visible(blocks::get)
        .build()
    );

    private final Setting<Action> aBlocks = leaveIf.add(new EnumSetting.Builder<Action>()
        .name("block-action")
        .description("Action to perform when inside a selected block.")
        .defaultValue(Action.Hub)
        .visible(blocks::get)
        .build()
    );

    private final Setting<String> cBlocks = leaveIf.add(new StringSetting.Builder()
        .name("block-message")
        .description("Message to send when block action is set to chat.")
        .defaultValue("")
        .visible(() -> blocks.get() && aBlocks.get() == Action.Chat)
        .build()
    );

    public AutoLeave() {
        super(Kopateli.CATEGORY, "auto-leave", "Automatically leaves the server when certain conditions are met.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre ignored) {
        if (!Utils.canUpdate()) return;

        if (players.get() && anyPlayerInRange(vPlayers.get()))
            process("player-nearby", aPlayers.get(), cPlayers.get());

        if (health.get() && mc.player.getHealth() <= vHealth.get())
            process("low-hp", aHealth.get(), cHealth.get());

        if (blocks.get() && isInsideSelectedBlock())
            process("in-block", aBlocks.get(), cBlocks.get());
    }

    private void process(String reason, Action action, String cmd) {
        if (autoDisable.get()) toggle();

        switch (action) {
            case Leave -> mc.disconnect(Text.literal("AutoLeave | " + reason));
            case Hub -> send("/hub");
            case Chat -> send(cmd);
        }
    }

    private boolean anyPlayerInRange(float range) {
        for (PlayerEntity e : mc.world.getPlayers()) {
            if (mc.player != e && EntityPredicates.EXCEPT_SPECTATOR.test(e)
                && EntityPredicates.VALID_LIVING_ENTITY.test(e)
                && e.squaredDistanceTo(mc.player) < range * range)
                    return true;
        }

        return false;
    }

    private boolean isInsideSelectedBlock() {
        if (vBlocks.get().isEmpty()) return false;
        return vBlocks.get().contains(mc.world.getBlockState(mc.player.getBlockPos()).getBlock());
    }

    private void send(String msg) {
        if (msg.isBlank()) return;
        if (this.sendTimer != -1 && System.currentTimeMillis() - this.sendTimer < 1000) return;
        if (msg.startsWith("/")) mc.player.networkHandler.sendChatCommand(msg.substring(1));
        else mc.player.networkHandler.sendChatMessage(msg);

        this.sendTimer = System.currentTimeMillis();
    }

    public enum Action {
        Leave, Hub, Chat
    }
}
