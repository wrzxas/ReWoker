package org.wrzxas.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.wrzxas.events.FireworkEvent;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends ProjectileEntity {
    protected FireworkRocketEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    private void tick(LivingEntity shooter, Vec3d originalVelocity) {
        if (!shooter.isGliding()) {
            shooter.setVelocity(originalVelocity);
            return;
        }

        FireworkEvent e = MeteorClient.EVENT_BUS.post(new FireworkEvent(this, 1.5, 1.5));
        double boostX = e.x;
        double boostY = e.y;
        Vec3d look = shooter.getRotationVector();
        Vec3d motion = shooter.getVelocity();

        if (!e.isCancelled()) {
            shooter.setVelocity(motion.add(look.x * 0.1 + (look.x * boostX - motion.x) * 0.5,
                look.y * 0.1 + (look.y * boostY - motion.y) * 0.5,
                look.z * 0.1 + (look.z * boostX - motion.z) * 0.5));
        } else shooter.setVelocity(new Vec3d(look.x * (boostX + 0.2),
                look.y * (boostY + 0.2), look.z * (boostX + 0.2)));
    }
}
