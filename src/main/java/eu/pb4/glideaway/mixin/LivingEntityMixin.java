package eu.pb4.glideaway.mixin;

import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.util.GlideGamerules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean isDead();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "stopRiding", at = @At("HEAD"), cancellable = true)
    private void cancelStopRiding(CallbackInfo ci) {
        if (this.getVehicle() instanceof GliderEntity entity && entity.hasCurseOfBinding() && !entity.hasLanded() && !this.isDead()) {
            ci.cancel();
        }
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "onDismounted", at = @At("HEAD"))
    private void returnGlider(Entity vehicle, CallbackInfo ci) {
        if (this.getEntityWorld() instanceof ServerWorld world && vehicle instanceof GliderEntity entity) {
            if (world.getGameRules().getBoolean(GlideGamerules.PICK_HANG_GLIDER) || entity.hasCurseOfBinding()) {
                entity.giveOrDrop(this);
            }
            this.setVelocity(entity.getVelocity());
            this.velocityModified = true;
        }
    }
}
