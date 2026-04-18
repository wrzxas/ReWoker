package org.wrzxas.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.world.Dimension;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.wrzxas.util.EndCityLocator;

import java.util.ArrayList;
import java.util.List;

public class FinderCommand extends Command {
    public final List<Pos> points = new ArrayList<>();

    public FinderCommand() {
        super("elytrafinder", "Automatically set waypoints to end city");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("seed", LongArgumentType.longArg()).executes(ctx -> {
            long seed = LongArgumentType.getLong(ctx, "seed");
            List<EndCityLocator.Candidate> candidates = EndCityLocator.findEndCities(seed, 0, 0, 15000);
            Waypoints wp = Waypoints.get();
            for (var candidate : candidates) {
                int x = candidate.blockX();
                int z = candidate.blockZ();
                Waypoint waypoint = new Waypoint.Builder()
                    .name("city")
                    .pos(new BlockPos(x, 96, z))
                    .dimension(Dimension.End)
                    .build();
                wp.add(waypoint);
                points.add(new Pos(x, z));
            }
            return SINGLE_SUCCESS;
        }));
    }

    public record Pos(int x, int z) {
    }
}
