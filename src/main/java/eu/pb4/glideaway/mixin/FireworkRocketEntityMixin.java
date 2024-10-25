package eu.pb4.glideaway.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.util.GlideGamerules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity {
    @Shadow
    private @Nullable LivingEntity shooter;

    @Shadow private int life;

    @Shadow private int lifeTime;

    public FireworkRocketEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Vec3d;ZERO:Lnet/minecraft/util/math/Vec3d;", ordinal = 0))
    private Vec3d pushGliderWithFirework(Vec3d original) {
        if (this.getWorld() instanceof ServerWorld serverWorld && this.shooter != null && this.shooter.getVehicle() instanceof GliderEntity glider && serverWorld.getGameRules().getBoolean(GlideGamerules.ALLOW_FIREWORK_BOOST)) {
            var rotationVector = glider.getRotationVector();
            var velocity = glider.getVelocity();

            glider.setVelocity(
                    velocity.add(
                            rotationVector.x * 0.1 + (rotationVector.x * 1.5 - velocity.x) * 0.5, rotationVector.y * 0.1 + (rotationVector.y * 1.5 - velocity.y) * 0.5, rotationVector.z * 0.1 + (rotationVector.z * 1.5 - velocity.z) * 0.5
                    )
            );

            if (this.age % 2 == 0) {
                glider.damageStack(1);
            }

            this.lifeTime -= 1;

            return new Vec3d(0, 1.2, 0);
        }

        return original;
    }
}
