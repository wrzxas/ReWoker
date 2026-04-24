package org.wrzxas;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.wrzxas.modules.*;

public class ReWoker extends MeteorAddon {
    public static final Category CATEGORY = new Category("RE_WOKER");

    @Override
    public void onInitialize() {
        Modules m = Modules.get();
        m.add(new AutoRTP());
        m.add(new AutoLeave());
        m.add(new AutoSkid());
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
