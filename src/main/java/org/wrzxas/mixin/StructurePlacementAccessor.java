package org.wrzxas.mixin;

import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructurePlacement.class)
public interface StructurePlacementAccessor {
    @Invoker("getSalt")
    int kopateli$getSalt();
}
