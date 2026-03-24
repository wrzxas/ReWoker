package org.wrzxas;

import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.wrzxas.modules.AutoRTP;

public class AutoRTPAddon extends MeteorAddon {
    public static final Category CATEGORY = new Category("ByExpiry");

    @Override
    public void onInitialize() {
        Modules.get().add(new AutoRTP());
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
        return new GithubRepo("wrzxas", "AutoRTP");
    }
}
