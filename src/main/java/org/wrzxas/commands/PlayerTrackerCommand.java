package org.wrzxas.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.commands.commands.FriendsCommand;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import org.wrzxas.systems.PlayerTracker;

public class PlayerTrackerCommand extends Command {
    public PlayerTrackerCommand() {
        super("playertracker", "Manages player tracker.", "ptracker");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add")
            .then(argument("player", StringArgumentType.word())
                .suggests((context, builder1) -> {
                    for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList())
                        builder1.suggest(entry.getProfile().name());
                    return builder1.buildFuture();
                }).executes(context -> {
                    String name = StringArgumentType.getString(context, "player");

                    if (PlayerTracker.get().add(name))
                        ChatUtils.sendMsg(name.hashCode(), Formatting.GRAY, "Added (highlight)%s (default)to tracker.".formatted(name));
                    else error("Player already tracked or invalid.");
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("remove")
            .then(argument("player", StringArgumentType.word())
                .suggests((context, builder1) -> {
                    PlayerTracker.get().forEach(builder1::suggest);
                    return builder1.buildFuture();
                }).executes(context -> {
                    PlayerListEntry entry = PlayerListEntryArgumentType.get(context);
                    String name = entry.getProfile().name();

                    if (PlayerTracker.get().remove(name))
                        ChatUtils.sendMsg(name.hashCode(), Formatting.GRAY, "Removed (highlight)%s (default)from tracker.".formatted(name));
                    else error("Player not found.");
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("list").executes(context -> {
            info("--- Tracked Players ((highlight)%s(default)) ---", PlayerTracker.get().count());

            PlayerTracker.get().forEach(player -> ChatUtils.info("(highlight)%s".formatted(player)));

            return SINGLE_SUCCESS;
        }));
    }
}
