package org.wrzxas.util;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import org.wrzxas.mixin.StructurePlacementAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public final class EndCityLocator {
    private EndCityLocator() {}

    public record Candidate(int regionX, int regionZ, int chunkX, int chunkZ, int blockX, int blockZ) {}

    public static RandomSpreadStructurePlacement getEndCityPlacement() {
        Registry<StructureSet> structureSets =
            mc.world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE_SET);

        for (RegistryEntry.Reference<StructureSet> setEntry : structureSets.streamEntries().toList()) {
            StructureSet set = setEntry.value();

            for (StructureSet.WeightedEntry weighted : set.structures()) {
                RegistryKey<Structure> key = weighted.structure()
                    .getKey()
                    .orElse(null);

                if (Objects.equals(key, StructureKeys.END_CITY)) {
                    StructurePlacement placement = set.placement();

                    if (placement instanceof RandomSpreadStructurePlacement spread) {
                        return spread;
                    }

                    throw new IllegalStateException(
                        "End City placement exists, but is not RandomSpreadStructurePlacement: "
                            + placement.getClass().getName()
                    );
                }
            }
        }

        throw new IllegalStateException("End City StructureSet not found");
    }

    public static int getEndCitySpacing() {
        return getEndCityPlacement().getSpacing();
    }

    public static int getEndCitySeparation() {
        return getEndCityPlacement().getSeparation();
    }

    public static int getEndCitySalt() {
        return ((StructurePlacementAccessor) getEndCityPlacement()).kopateli$getSalt();
    }

    public static List<Candidate> findEndCities(long seed, int cx, int cz, int radius) {
        RandomSpreadStructurePlacement placement = getEndCityPlacement();

        int spacing = placement.getSpacing();

        int minChunkX = Math.floorDiv(cx - radius, 16);
        int maxChunkX = Math.floorDiv(cx + radius, 16);
        int minChunkZ = Math.floorDiv(cz - radius, 16);
        int maxChunkZ = Math.floorDiv(cz + radius, 16);

        int minRegionX = Math.floorDiv(minChunkX, spacing);
        int maxRegionX = Math.floorDiv(maxChunkX, spacing);
        int minRegionZ = Math.floorDiv(minChunkZ, spacing);
        int maxRegionZ = Math.floorDiv(maxChunkZ, spacing);

        long radiusSq = (long) radius * radius;
        List<Candidate> out = new ArrayList<>();

        for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
            for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                // любой чанк внутри региона годится; getStartChunk сам floorDiv'ит к region coords
                ChunkPos start = placement.getStartChunk(seed, regionX * spacing, regionZ * spacing);

                int blockX = start.getStartX();
                int blockZ = start.getStartZ();

                long dx = (long) blockX - cx;
                long dz = (long) blockZ - cz;

                if (dx * dx + dz * dz <= radiusSq) {
                    out.add(new Candidate(
                        regionX,
                        regionZ,
                        start.x,
                        start.z,
                        blockX,
                        blockZ
                    ));
                }
            }
        }

        return out;
    }
}
