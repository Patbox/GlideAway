package eu.pb4.glideaway.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.util.GlideGamerules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {
    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isGliding()Z"))
    private boolean allowUsageOfFirework(boolean original, @Local(argsOnly = true) PlayerEntity player) {
        return original || (player.getWorld() instanceof ServerWorld world && world.getGameRules().getBoolean(GlideGamerules.ALLOW_FIREWORK_BOOST) && player.getVehicle() instanceof GliderEntity);
    }

    @ModifyReturnValue(method = "use", at = @At(value = "RETURN"))
    private ActionResult fixAnimation(ActionResult original, @Local(argsOnly = true) PlayerEntity player) {
        return original == ActionResult.SUCCESS && player.getVehicle() instanceof GliderEntity ? ActionResult.SUCCESS_SERVER : original;
    }
}
