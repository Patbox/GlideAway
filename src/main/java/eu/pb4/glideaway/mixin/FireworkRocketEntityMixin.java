package eu.pb4.glideaway.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.util.GlideGamerules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity {
    @Shadow
    private @Nullable LivingEntity attachedToEntity;

    @Shadow private int life;

    @Shadow private int lifetime;

    public FireworkRocketEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;", ordinal = 0))
    private Vec3 pushGliderWithFirework(Vec3 original) {
        if (this.level() instanceof ServerLevel serverWorld && this.attachedToEntity != null && this.attachedToEntity.getVehicle() instanceof GliderEntity glider && serverWorld.getGameRules().get(GlideGamerules.ALLOW_FIREWORK_BOOST)) {
            var rotationVector = glider.getLookAngle();
            var velocity = glider.getDeltaMovement();

            glider.setDeltaMovement(
                    velocity.add(
                            rotationVector.x * 0.1 + (rotationVector.x * 1.5 - velocity.x) * 0.5, rotationVector.y * 0.1 + (rotationVector.y * 1.5 - velocity.y) * 0.5, rotationVector.z * 0.1 + (rotationVector.z * 1.5 - velocity.z) * 0.5
                    )
            );

            if (this.tickCount % 2 == 0) {
                glider.damageStack(1);
            }

            this.lifetime -= 1;

            return new Vec3(0, 1.2, 0);
        }

        return original;
    }
}
