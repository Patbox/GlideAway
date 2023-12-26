package eu.pb4.glideaway.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow @Final private static int FALL_FLYING_FLAG;

    @Shadow protected abstract void initDataTracker();

    @Shadow public abstract ItemStack getMainHandStack();

    @Shadow public abstract void setStackInHand(Hand hand, ItemStack stack);

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /*@SuppressWarnings("ConstantValue")
    @Inject(method = "onDismounted", at = @At("HEAD"))
    private void returnGlider(Entity vehicle, CallbackInfo ci) {
        if (vehicle instanceof GliderEntity entity) {

            entity.discard();
        }
    }

    /*@Inject(method = "tickFallFlying", at = @At("HEAD"), cancellable = true)
    private void fakeFallFlying(CallbackInfo ci) {
        if (this.getVehicle() instanceof GliderEntity glider) {
            this.setFlag(FALL_FLYING_FLAG, true);
            ci.cancel();
        }
    }*/
}
