package eu.pb4.glideaway.mixin;

import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.util.GlideGamerules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean isDeadOrDying();

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "stopRiding", at = @At("HEAD"), cancellable = true)
    private void cancelStopRiding(CallbackInfo ci) {
        if (this.getVehicle() instanceof GliderEntity entity && entity.hasCurseOfBinding() && !entity.hasLanded() && !this.isDeadOrDying()) {
            ci.cancel();
        }
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "dismountVehicle", at = @At("HEAD"))
    private void returnGlider(Entity vehicle, CallbackInfo ci) {
        if (this.level() instanceof ServerLevel world && vehicle instanceof GliderEntity entity) {
            if (world.getGameRules().get(GlideGamerules.PICK_HANG_GLIDER) || entity.hasCurseOfBinding()) {
                entity.giveOrDrop(this);
            }
            this.setDeltaMovement(entity.getDeltaMovement());
            this.hurtMarked = true;
        }
    }
}
