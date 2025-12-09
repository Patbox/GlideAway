package eu.pb4.glideaway.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.glideaway.ModInit;
import eu.pb4.glideaway.entity.GliderEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow public abstract void awardStat(Stat<?> stat, int amount);

    @Inject(method = "checkRidingStatistics", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerPlayer;getVehicle()Lnet/minecraft/world/entity/Entity;", shift = At.Shift.AFTER))
    private void gliderRidingMotionStat(double deltaX, double deltaY, double deltaZ, CallbackInfo ci, @Local Entity entity, @Local int distance) {
        if (entity instanceof GliderEntity) {
            this.awardStat(Stats.CUSTOM.get(ModInit.GLIDE_ONE_CM), distance);
        }
    }
}
