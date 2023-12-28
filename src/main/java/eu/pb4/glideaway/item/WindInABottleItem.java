package eu.pb4.glideaway.item;

import eu.pb4.factorytools.api.item.ModeledItem;
import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.mixin.ServerPlayNetworkHandlerAccessor;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WindInABottleItem extends ModeledItem {
    private final boolean consume;

    public WindInABottleItem(Settings settings, boolean consume) {
        super(settings);
        this.consume = consume;
    }

    public ActionResult useOnEntityEvent(PlayerEntity user, World world, Hand hand, Entity entity, EntityHitResult result) {
        var stack = user.getStackInHand(hand);
        var hasWindCharge = world.getEnabledFeatures().contains(FeatureFlags.UPDATE_1_21);

        if (!world.isClient && hasWindCharge && entity instanceof WindChargeEntity windChargeEntity && stack.isOf(Items.GLASS_BOTTLE)) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            world.emitGameEvent(user, GameEvent.FLUID_PICKUP, user.getPos());
            if (user instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(serverPlayerEntity, stack, windChargeEntity);
                serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            }

            stack.decrement(1);
            user.getInventory().offerOrDrop(new ItemStack(this));
            windChargeEntity.discard();
            return ActionResult.SUCCESS;
        } else if (!world.isClient && !hasWindCharge && entity instanceof SmallFireballEntity fireballEntity && stack.isOf(Items.POTION) && PotionUtil.getPotion(stack) == Potions.EMPTY) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            world.emitGameEvent(user, GameEvent.FLUID_PICKUP, user.getPos());
            if (user instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(serverPlayerEntity, stack, fireballEntity);
                serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            }

            stack.decrement(1);
            user.getInventory().offerOrDrop(new ItemStack(this));
            fireballEntity.discard();
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return !this.consume;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.getRootVehicle() instanceof GliderEntity glider) {
            glider.addVelocity(Vec3d.fromPolar(glider.getPitch() - 90, glider.getYaw()).multiply(1, 0.6, 1).normalize().multiply(0.8));
        } else {
            user.addVelocity(user.getRotationVector().multiply(1, 0.5, 1).normalize().negate().multiply(0.8));
            user.velocityModified = true;
            user.limitFallDistance();
            if (user instanceof ServerPlayerEntity player) {
                ((ServerPlayNetworkHandlerAccessor) player.networkHandler).setFloatingTicks(0);
            }
        }
        user.getItemCooldownManager().set(this, 20);

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.GUST_EMITTER, user.getX(), user.getY(), user.getZ(), 0, 0, 0, 0, 0);
            serverWorld.playSoundFromEntity(null, user, SoundEvents.ENTITY_GENERIC_WIND_BURST, SoundCategory.PLAYERS, 0.5f, 0);
        }

        if (this.consume && !user.isCreative()) {
            user.getStackInHand(hand).decrement(1);
            user.getInventory().offerOrDrop(new ItemStack(Items.GLASS_BOTTLE));
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
