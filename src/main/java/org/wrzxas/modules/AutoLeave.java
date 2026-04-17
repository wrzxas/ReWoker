package org.wrzxas.modules;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
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
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup leaveIf = settings.createGroup("Leave if");

    private final Setting<Boolean> antiKtLeave = sgGeneral.add(new BoolSetting.Builder()
        .name("anti-kt-leave")
        .description("Prevents leaving in combat.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-disable")
        .description("Disables module after action.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> players = leaveIf.add(new BoolSetting.Builder()
        .name("player-nearby")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> vPlayers = leaveIf.add(new IntSetting.Builder()
        .name("distance-to-player")
        .defaultValue(50)
        .range(5, 300)
        .sliderRange(5, 300)
        .visible(players::get)
        .build()
    );

    private final Setting<Action> aPlayers = leaveIf.add(new EnumSetting.Builder<Action>()
        .name("player-action")
        .defaultValue(Action.Hub)
        .visible(players::get)
        .build()
    );

    private final Setting<String> cPlayers = leaveIf.add(new StringSetting.Builder()
        .name("player-message")
        .defaultValue("")
        .visible(() -> players.get() && aPlayers.get() == Action.Chat)
        .build()
    );

    private final Setting<Boolean> health = leaveIf.add(new BoolSetting.Builder()
        .name("low-hp")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> vHealth = leaveIf.add(new DoubleSetting.Builder()
        .name("hp")
        .defaultValue(4)
        .range(0, 20)
        .sliderRange(0, 20)
        .visible(health::get)
        .build()
    );

    private final Setting<Action> aHealth = leaveIf.add(new EnumSetting.Builder<Action>()
        .name("health-action")
        .defaultValue(Action.Hub)
        .visible(health::get)
        .build()
    );

    private final Setting<String> cHealth = leaveIf.add(new StringSetting.Builder()
        .name("health-message")
        .defaultValue("")
        .visible(() -> health.get() && aHealth.get() == Action.Chat)
        .build()
    );

    private final Setting<Boolean> blocks = leaveIf.add(new BoolSetting.Builder()
        .name("in-block")
        .defaultValue(false)
        .build()
    );

    private final Setting<List<Block>> vBlocks = leaveIf.add(new BlockListSetting.Builder()
        .name("blocks")
        .visible(blocks::get)
        .build()
    );

    private final Setting<Action> aBlocks = leaveIf.add(new EnumSetting.Builder<Action>()
        .name("blocks-action")
        .defaultValue(Action.Hub)
        .visible(blocks::get)
        .build()
    );

    private final Setting<String> cBlocks = leaveIf.add(new StringSetting.Builder()
        .name("blocks-message")
        .defaultValue("")
        .visible(() -> blocks.get() && aBlocks.get() == Action.Chat)
        .build()
    );

    private final Setting<Boolean> staff = leaveIf.add(new BoolSetting.Builder()
        .name("staff")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> vStaff = leaveIf.add(new DoubleSetting.Builder()
        .name("only-if-spec")
        .defaultValue(4)
        .range(0, 20)
        .sliderRange(0, 20)
        .visible(staff::get)
        .build()
    );

    private final Setting<Action> aStaff = leaveIf.add(new EnumSetting.Builder<Action>()
        .name("staff-action")
        .defaultValue(Action.Hub)
        .visible(staff::get)
        .build()
    );

    private final Setting<String> cStaff = leaveIf.add(new StringSetting.Builder()
        .name("staff-message")
        .defaultValue("")
        .visible(() -> staff.get() && aStaff.get() == Action.Chat)
        .build()
    );

    public AutoLeave() {
        super(Kopateli.CATEGORY, "auto-leave", "desc");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        if (players.get() && anyPlayerInRange(vPlayers.get())) {
            process("player-nearby", aPlayers.get(), cPlayers.get());
        }

        if (health.get() && mc.player.getHealth() <= vHealth.get()) {
            process("low-hp", aHealth.get(), cHealth.get());
        }

        if (blocks.get() && isInsideSelectedBlock()) {
            process("in-block", aBlocks.get(), cBlocks.get());
        }

        /*if (staff.get() && Managers.staff.anyOnline(vStaff.get())) TODO staff manager
            process("staff", aStaff.get(), cStaff.get());*/
    }

    private void process(String reason, Action action, String cmd) {
        if (autoDisable.get()) toggle();
        /*if (antiKtLeave.get() && Managers.combat.inPvp()) return; TODO combat manager*/

        switch (action) {
            case Leave -> mc.getNetworkHandler().getConnection().disconnect(Text.literal("AutoLeave | " + reason));
            case Hub -> mc.player.networkHandler.sendChatCommand("hub");
            case Chat -> {
                if (cmd.isBlank()) break;
                if (cmd.startsWith("/")) mc.player.networkHandler.sendChatCommand(cmd.substring(1));
                else mc.player.networkHandler.sendChatMessage(cmd);
            }
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

    public enum Action {
        Leave, Hub, Chat
    }
}
