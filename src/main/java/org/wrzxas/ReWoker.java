package org.wrzxas;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.wrzxas.commands.PlayerTrackerCommand;
import org.wrzxas.modules.*;
import org.wrzxas.systems.PlayerTracker;

public class ReWoker extends MeteorAddon {
    public static final Category CATEGORY = new Category("RE_WOKER");

    @Override
    public void onInitialize() {
        Systems.add(new PlayerTracker());

        initModules();
        initCommands();
    }

    private void initModules() {
        Modules m = Modules.get();
        m.add(new AutoRTP());
        m.add(new AutoLeave());
        m.add(new AutoSkid());
        m.add(new ElytraBooster());
        m.add(new PlayerTrackerModule());
    }

    private void initCommands() {
        Commands.add(new PlayerTrackerCommand());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "org.wrzxas";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("wrzxas", "ReWoker");
    }
}
