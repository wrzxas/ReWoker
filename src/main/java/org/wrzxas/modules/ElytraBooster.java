package org.wrzxas.modules;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.MathHelper;
import org.wrzxas.ReWoker;
import org.wrzxas.events.FireworkEvent;

public class ElytraBooster extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgYaw = this.settings.createGroup("Yaw");
    private final SettingGroup sgPitch = this.settings.createGroup("Pitch");

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("boost-mode")
        .description("Boost mode.")
        .defaultValue(Mode.Fixed)
        .build()
    );

    private final Setting<Double> boost = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed-boost")
        .description("Fixed boost value.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Fixed)
        .build()
    );

    private final Setting<Double> yaw5 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-0-5")
        .description("Speed boost when yaw is between 0 and 5 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> yaw10 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-5-10")
        .description("Speed boost when yaw is between 5 and 10 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> yaw15 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-10-15")
        .description("Speed boost when yaw is between 10 and 15 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> yaw20 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-15-20")
        .description("Speed boost when yaw is between 15 and 20 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> yaw25 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-20-25")
        .description("Speed boost when yaw is between 20 and 25 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> yaw30 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-25-30")
        .description("Speed boost when yaw is between 25 and 30 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> yaw35 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-30-35")
        .description("Speed boost when yaw is between 30 and 35 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> yaw40 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-35-40")
        .description("Speed boost when yaw is between 35 and 40 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> yaw45 = sgYaw.add(new DoubleSetting.Builder()
        .name("yaw-40-45")
        .description("Speed boost when yaw is between 40 and 45 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch5 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-0-5")
        .description("Speed boost when pitch is between 0 and 5 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch10 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-5-10")
        .description("Speed boost when pitch is between 5 and 10 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch15 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-10-15")
        .description("Speed boost when pitch is between 10 and 15 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch20 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-15-20")
        .description("Speed boost when pitch is between 15 and 20 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch25 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-20-25")
        .description("Speed boost when pitch is between 20 and 25 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch30 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-25-30")
        .description("Speed boost when pitch is between 25 and 30 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch35 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-30-35")
        .description("Speed boost when pitch is between 30 and 35 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch40 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-35-40")
        .description("Speed boost when pitch is between 35 and 40 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    private final Setting<Double> pitch45 = sgPitch.add(new DoubleSetting.Builder()
        .name("pitch-40-45")
        .description("Speed boost when pitch is between 40 and 45 degrees.")
        .range(1.5, 2.5)
        .sliderRange(1.5, 2.5)
        .defaultValue(1.5)
        .visible(() -> mode.get() == Mode.Custom)
        .build()
    );

    public ElytraBooster() {
        super(ReWoker.CATEGORY, "elytra-booster", "Speeds you up while flying with elytra.");
    }

    @EventHandler
    public void onEvent(FireworkEvent e) {
        double yawSpeed = getBoostX();
        double pitchSpeed = getBoostY();

        if (pitchSpeed > yawSpeed)
            yawSpeed = pitchSpeed;

        e.x = yawSpeed;
        e.y = pitchSpeed;
    }

    private double getBoostX() {
        if (mode.get() == Mode.Fixed) return boost.get();
        float yaw = range(MathHelper.wrapDegrees(mc.player.getYaw()));
        if (yaw >= 0 && yaw < 5) return yaw5.get();
        if (yaw >= 5 && yaw < 10) return yaw10.get();
        if (yaw >= 10 && yaw < 15) return yaw15.get();
        if (yaw >= 15 && yaw < 20) return yaw20.get();
        if (yaw >= 20 && yaw < 25) return yaw25.get();
        if (yaw >= 25 && yaw < 30) return yaw30.get();
        if (yaw >= 30 && yaw < 35) return yaw35.get();
        if (yaw >= 35 && yaw < 40) return yaw40.get();
        if (yaw >= 40 && yaw <= 45) return yaw45.get();
        return 1.5;
    }

    private double getBoostY() {
        if (mode.get() == Mode.Fixed) return boost.get();
        float pitch = range(mc.player.getPitch());
        if (pitch >= 0 && pitch < 5) return pitch5.get();
        if (pitch >= 5 && pitch < 10) return pitch10.get();
        if (pitch >= 10 && pitch < 15) return pitch15.get();
        if (pitch >= 15 && pitch < 20) return pitch20.get();
        if (pitch >= 20 && pitch < 25) return pitch25.get();
        if (pitch >= 25 && pitch < 30) return pitch30.get();
        if (pitch >= 30 && pitch < 35) return pitch35.get();
        if (pitch >= 35 && pitch < 40) return pitch40.get();
        if (pitch >= 40 && pitch <= 45) return pitch45.get();
        return 1.5;
    }

    private float range(float v) {
        v = Math.abs(v);
        if (v >= 90) v = 180 - v;
        if (v >= 45) v = 90 - v;
        return v;
    }

    public enum Mode {
        Fixed, Custom
    }
}
