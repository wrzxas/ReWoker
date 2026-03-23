package org.wrzxas.settings;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.function.Consumer;

public class BiomeListSetting extends Setting<Set<RegistryKey<Biome>>> {
    public BiomeListSetting(String name, String description, Set<RegistryKey<Biome>> defaultValue, Consumer<Set<RegistryKey<Biome>>> onChanged, Consumer<Setting<Set<RegistryKey<Biome>>>> onModuleActivated, IVisible visible) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);
    }

    @Override
    protected Set<RegistryKey<Biome>> parseImpl(String str) {
        String[] values = str.split(",");
        Set<RegistryKey<Biome>> list = new HashSet<>(values.length);

        for (String val : values) {
            String name = val.trim();
            Identifier id = name.contains(":") ? Identifier.of(name) : Identifier.ofVanilla(name);
            list.add(RegistryKey.of(RegistryKeys.BIOME, id));
        }
        return list;
    }

    @Override
    protected boolean isValueValid(Set<RegistryKey<Biome>> value) {
        return true;
    }

    @Override
    public Iterable<Identifier> getIdentifierSuggestions() {
        return Optional.ofNullable(MinecraftClient.getInstance().getNetworkHandler())
            .flatMap(networkHandler -> networkHandler.getRegistryManager().getOptional(RegistryKeys.BIOME))
            .map(Registry::getIds).orElse(Set.of());
    }

    @Override
    protected void resetImpl() {
        value = new ObjectOpenHashSet<>(defaultValue);
    }

    @Override
    public NbtCompound save(NbtCompound tag) {
        NbtList valueTag = new NbtList();
        for (RegistryKey<Biome> biome : get()) {
            valueTag.add(NbtString.of(biome.getValue().toString()));
        }
        tag.put("value", valueTag);

        return tag;
    }

    @Override
    public Set<RegistryKey<Biome>> load(NbtCompound tag) {
        get().clear();

        for (NbtElement tagI : tag.getListOrEmpty("value"))
            get().add(RegistryKey.of(RegistryKeys.BIOME, Identifier.of(tagI.asString().orElse(""))));

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, Set<RegistryKey<Biome>>, BiomeListSetting> {
        public Builder() {
            super(new ObjectOpenHashSet<>());
        }

        @Override
        public BiomeListSetting build() {
            return new BiomeListSetting(name, description, defaultValue, onChanged, onModuleActivated, visible);
        }
    }
}
