package org.wrzxas.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.wrzxas.Kopateli;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class AutoSkid extends Module {
    private long lastSkid = -1; // drop/store
    private long lastTp = -1; // place
    private long lastAction = -1; // post actions

    private State state = State.IDLE;
    private int actionIdx = 0;
    private int hash = -1;

    private boolean wasBaritone = false;

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgActions = this.settings.createGroup("After skid");

    private final Setting<DelayMode> delayMode = sgGeneral.add(new EnumSetting.Builder<DelayMode>()
        .name("delay-mode")
        .description("When to start the cycle.")
        .defaultValue(DelayMode.Timer)
        .build()
    );

    private final Setting<Integer> skidDelay = sgGeneral.add(new IntSetting.Builder()
        .name("skid-delay")
        .description("Delay between each skid action.")
        .range(0, 12000)
        .sliderRange(0, 200)
        .defaultValue(0)
        .build()
    );

    private final Setting<Integer> timerDelay = sgGeneral.add(new IntSetting.Builder()
        .name("timer-delay")
        .description("How long to wait before start skid.")
        .range(0, 200)
        .defaultValue(6000)
        .visible(() -> delayMode.get() == DelayMode.Timer)
        .build()
    );

    private final Setting<Boolean> pauseBaritone = sgGeneral.add(new BoolSetting.Builder()
        .name("pause-baritone")
        .description("Paused baritone when skid items.")
        .defaultValue(true)
        .build()
    );

    private final Setting<String> skidPlace = sgActions.add(new StringSetting.Builder()
        .name("skid-place")
        .description("Command used to teleport to the skid place.")
        .defaultValue("/home")
        .build()
    );

    private final Setting<List<Item>> skidItems = sgActions.add(new ItemListSetting.Builder()
        .name("skid-items")
        .description("Items to skid from your inventory.")
        .build()
    );

    private final Setting<List<String>> postActions = sgActions.add(new StringListSetting.Builder()
        .name("actions")
        .description("Actions to run after skid items.")
        .defaultValue("/rtp")
        .build()
    );

    private final Setting<Integer> actionsDelay = sgActions.add(new IntSetting.Builder()
        .name("actions-delay")
        .description("Delay between each action.")
        .range(0, 50)
        .defaultValue(10)
        .build()
    );

    public AutoSkid() {
        super(Kopateli.CATEGORY, "auto-skid", "Automatically teleports, skids selected items, and runs actions after skid.");
    }

    @Override
    public void onActivate() {
        hash = -1;
        lastTp = -1;
        lastSkid = -1;
        lastAction = -1;
        state = State.IDLE;
        actionIdx = 0;
    }

    @Override
    public void onDeactivate() {
        hash = -1;
        lastTp = -1;
        lastSkid = -1;
        lastAction = -1;
        state = State.IDLE;
        actionIdx = 0;
    }

    @EventHandler
    public void onEvent(TickEvent.Pre ignored) {
        if (!Utils.canUpdate()) return;
        int hash = postActions.get().hashCode();
        if (this.hash != hash) {
            this.hash = hash;
            actionIdx = 0;
        }
        switch (state) {
            case IDLE -> {
                if (shouldSkid()) state = State.TP;
            }
            case TP -> {
                send(skidPlace.get());
                lastTp = mc.player.age;
                state = State.SKID;
            }
            case SKID -> {
                if (skid()) state = State.POST;
            }
            case POST -> {
                if (actions()) {
                    lastTp = mc.player.age;
                    state = State.IDLE;
                }
            }
        }
    }

    private boolean skid() {
        if (pauseBaritone.get() && PathManagers.get().isPathing() && !wasBaritone) {
            wasBaritone = true;
            PathManagers.get().pause();
        }
        if (mc.player.age - lastSkid < skidDelay.get()) return false;
        FindItemResult find = InvUtils.find(skidItems.get().toArray(new Item[0]));
        if (!find.found()) return true;
        InvUtils.drop().slot(find.slot());
        lastSkid = mc.player.age;
        return false;
    }

    private boolean actions() {
        if (mc.player.age - lastAction < actionsDelay.get()) return false;
        List<String> actions = postActions.get();
        if (actionIdx >= actions.size()) {
            actionIdx = 0;
            return true;
        }
        String action = actions.get(actionIdx++);
        send(action);
        lastAction = mc.player.age;
        return false;
    }

    private boolean shouldSkid() {
        if (pauseBaritone.get() && wasBaritone) {
            wasBaritone = false;
            PathManagers.get().resume();
        }
        if (delayMode.get() == DelayMode.Timer)
            return lastTp != -1 && mc.player.age - lastTp >= timerDelay.get();

        else if (delayMode.get() == DelayMode.FullInv)
            return isFullInv();

        return false;
    }

    private boolean isFullInv() {
        boolean hasItem = false;
        for (int i = 0; i < 36; i++) {
            ItemStack is = mc.player.getInventory().getStack(i);
            if (is.isEmpty()) return false;

            if (skidItems.get().contains(is.getItem())) {
                hasItem = true;
                if (is.getCount() < is.getMaxCount())
                    return false;
            }
        }
        return hasItem;
    }

    private void send(String msg) {
        if (msg.startsWith("/")) mc.player.networkHandler.sendChatCommand(msg.substring(1));
        else if (!msg.isBlank()) mc.player.networkHandler.sendChatMessage(msg);
    }

    public enum DelayMode {
        FullInv, Timer;

        @Override
        public String toString() {
            String name = name();
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (i > 0 && Character.isUpperCase(c))
                    result.append(' ');
                result.append(c);
            }

            return result.toString();
        }
    }

    private enum State {
        IDLE, TP, SKID, POST
    }
}
