package eu.pb4.glideaway.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.util.GlideGamerules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireworkRocketItem.class)
public class FireworkRocketItemMixin {
    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isFallFlying()Z"))
    private boolean allowUsageOfFirework(boolean original, @Local(argsOnly = true) Player player) {
        return original || (player.level() instanceof ServerLevel world && world.getGameRules().get(GlideGamerules.ALLOW_FIREWORK_BOOST) && player.getVehicle() instanceof GliderEntity);
    }

    @ModifyReturnValue(method = "use", at = @At(value = "RETURN"))
    private InteractionResult fixAnimation(InteractionResult original, @Local(argsOnly = true) Player player) {
        return original == InteractionResult.SUCCESS && player.getVehicle() instanceof GliderEntity ? InteractionResult.SUCCESS_SERVER : original;
    }
}
