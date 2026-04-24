package org.wrzxas.events;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.entity.Entity;

public class FireworkEvent extends Cancellable {
    public Entity entity;
    public double x, y;

    public FireworkEvent(Entity entity, double x, double y) {
        this.entity = entity;
        this.x = x;
        this.y = y;
    }
}
