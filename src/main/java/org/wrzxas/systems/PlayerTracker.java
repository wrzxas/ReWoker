package org.wrzxas.systems;

import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PlayerTracker extends System<PlayerTracker> implements Iterable<String> {
    private final List<String> players = new ArrayList<>();

    public PlayerTracker() {
        super("players");
    }

    public static PlayerTracker get() {
        return Systems.get(PlayerTracker.class);
    }

    public boolean add(String player) {
        if (player.isEmpty() || player.contains(" ")) return false;
        if (get(player) != null) return false;

        players.add(player);
        save();
        return true;
    }

    public boolean remove(String player) {
        if (players.remove(player)) {
            save();
            return true;
        }
        return false;
    }

    public String get(String player) {
        for (String pl : players)
            if (pl.equalsIgnoreCase(player)) return pl;
        return null;
    }

    public String get(PlayerEntity player) {
        return player != null ? get(player.getName().toString()) : null;
    }

    public String get(PlayerListEntry player) {
        return get(player.getProfile().name());
    }

    public int count() {
        return players.size();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    @Override
    public @NotNull Iterator<String> iterator() {
        return players.iterator();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();

        for (String player : players)
            list.add(NbtString.of(player));

        tag.put("players", list);
        return tag;
    }

    @Override
    public PlayerTracker fromTag(NbtCompound tag) {
        players.clear();

        for (NbtElement itemTag : tag.getListOrEmpty("players")) {
            String player = ((NbtString) itemTag).value();
            if (player.isBlank()) continue;
            if (get(player) != null) continue;
            players.add(player);
        }

        Collections.sort(players);
        return this;
    }
}
