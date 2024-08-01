package eu.pb4.glideaway.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.glideaway.ModInit;
import eu.pb4.glideaway.entity.GliderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow public abstract void increaseStat(Stat<?> stat, int amount);

    @Inject(method = "increaseRidingMotionStats", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getVehicle()Lnet/minecraft/entity/Entity;", shift = At.Shift.AFTER))
    private void gliderRidingMotionStat(double deltaX, double deltaY, double deltaZ, CallbackInfo ci, @Local Entity entity, @Local int distance) {
        if (entity instanceof GliderEntity) {
            this.increaseStat(Stats.CUSTOM.getOrCreateStat(ModInit.GLIDE_ONE_CM), distance);
        }
    }
}
