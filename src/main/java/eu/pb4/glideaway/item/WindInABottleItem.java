package eu.pb4.glideaway.item;

import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.mixin.ServerGamePacketListenerImplAccessor;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class WindInABottleItem extends Item implements PolymerItem {
    private final boolean consume;

    public WindInABottleItem(Properties settings, boolean consume) {
        super(settings);
        this.consume = consume;
    }

    public InteractionResult useOnEntityEvent(Player user, Level world, InteractionHand hand, Entity entity, EntityHitResult result) {
        var stack = user.getItemInHand(hand);

        if (!world.isClientSide() && entity instanceof WindCharge windChargeEntity && stack.is(Items.GLASS_BOTTLE)) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
            world.gameEvent(user, GameEvent.FLUID_PICKUP, user.position());
            if (user instanceof ServerPlayer serverPlayerEntity) {
                CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(serverPlayerEntity, stack, windChargeEntity);
                serverPlayerEntity.awardStat(Stats.ITEM_USED.get(this));
            }

            stack.shrink(1);
            user.getInventory().placeItemBackInInventory(new ItemStack(this));
            windChargeEntity.discard();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return !this.consume;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        var stack = user.getItemInHand(hand);
        if (user.getRootVehicle() instanceof GliderEntity glider) {
            glider.push(Vec3.directionFromRotation(glider.getXRot() - 90, glider.getYRot()).multiply(1, 0.6, 1).normalize().scale(0.8));
        } else {
            user.push(user.getLookAngle().multiply(1, 0.5, 1).normalize().reverse().scale(0.8));
            user.hurtMarked = true;
            user.checkFallDistanceAccumulation();
            if (user instanceof ServerPlayer player) {
                ((ServerGamePacketListenerImplAccessor) player.connection).setAboveGroundTickCount(0);
            }
        }
        user.getCooldowns().addCooldown(stack, 20);

        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(ParticleTypes.GUST_EMITTER_SMALL, user.getX(), user.getY(), user.getZ(), 0, 0, 0, 0, 0);
            serverWorld.playSound(null, user, SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 0.5f, 0);
        }

        if (this.consume && !user.isCreative()) {
            stack.shrink(1);
            user.getInventory().placeItemBackInInventory(new ItemStack(Items.GLASS_BOTTLE));
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.MUSIC_DISC_BLOCKS;
    }
}
