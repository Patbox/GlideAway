package eu.pb4.glideaway.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.util.GlideGamerules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {
    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isFallFlying()Z"))
    private boolean allowUsageOfFirework(boolean original, @Local(argsOnly = true) PlayerEntity player) {
        return original || (player.getWorld().getGameRules().getBoolean(GlideGamerules.ALLOW_FIREWORK_BOOST) && player.getVehicle() instanceof GliderEntity);
    }

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;success(Ljava/lang/Object;Z)Lnet/minecraft/util/TypedActionResult;"))
    private boolean fixAnimation(boolean original, @Local(argsOnly = true) PlayerEntity player) {
        return original || (player.getVehicle() instanceof GliderEntity);
    }
}
