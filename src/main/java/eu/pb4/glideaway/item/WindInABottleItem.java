package eu.pb4.glideaway.item;

import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.mixin.ServerPlayNetworkHandlerAccessor;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import xyz.nucleoid.packettweaker.PacketContext;

public class WindInABottleItem extends Item implements PolymerItem {
    private final boolean consume;

    public WindInABottleItem(Settings settings, boolean consume) {
        super(settings);
        this.consume = consume;
    }

    public ActionResult useOnEntityEvent(PlayerEntity user, World world, Hand hand, Entity entity, EntityHitResult result) {
        var stack = user.getStackInHand(hand);

        if (!world.isClient && entity instanceof WindChargeEntity windChargeEntity && stack.isOf(Items.GLASS_BOTTLE)) {
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
        }

        return ActionResult.PASS;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return !this.consume;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
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
        user.getItemCooldownManager().set(stack, 20);

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.GUST_EMITTER_SMALL, user.getX(), user.getY(), user.getZ(), 0, 0, 0, 0, 0);
            serverWorld.playSoundFromEntity(null, user, SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(), SoundCategory.PLAYERS, 0.5f, 0);
        }

        if (this.consume && !user.isCreative()) {
            stack.decrement(1);
            user.getInventory().offerOrDrop(new ItemStack(Items.GLASS_BOTTLE));
        }

        return ActionResult.SUCCESS_SERVER;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.MUSIC_DISC_BLOCKS;
    }
}
