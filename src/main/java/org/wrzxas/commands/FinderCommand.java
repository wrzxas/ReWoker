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
            points.clear();
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
            sort(points);
            return SINGLE_SUCCESS;
        }));
    }

    private void sort(List<Pos> list) {
        if (list.size() <= 1) return;

        for (int i = 0; i < list.size() - 1; i++) {
            Pos current = list.get(i);

            int bestIndex = i + 1;
            int bestDist = distSq(current, list.get(bestIndex));

            for (int j = i + 2; j < list.size(); j++) {
                int dist = distSq(current, list.get(j));
                if (dist < bestDist) {
                    bestDist = dist;
                    bestIndex = j;
                }
            }

            // swap (i+1) <-> bestIndex
            if (bestIndex != i + 1) {
                Pos tmp = list.get(i + 1);
                list.set(i + 1, list.get(bestIndex));
                list.set(bestIndex, tmp);
            }
        }
    }

    private int distSq(Pos a, Pos b) {
        int x = a.x - b.x;
        int z = a.z - b.z;
        return x * x + z * z;
    }

    public record Pos(int x, int z) {
    }
}
