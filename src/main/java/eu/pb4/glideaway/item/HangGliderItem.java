package eu.pb4.glideaway.item;

import eu.pb4.factorytools.api.advancement.TriggerCriterion;
import eu.pb4.factorytools.api.item.ModeledItem;
import eu.pb4.glideaway.entity.GlideEntities;
import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.util.GlideSoundEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static eu.pb4.glideaway.ModInit.id;

public class HangGliderItem extends ModeledItem {
    public static final Identifier USE_TRIGGER = id("on_use");

    public HangGliderItem(Item item, Settings settings) {
        super(item, settings);

        DispenserBlock.registerBehavior(this, new ItemDispenserBehavior() {
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                return GliderEntity.createDispenser(pointer, stack);
            }
        });
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.PHANTOM_MEMBRANE);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        if (GliderEntity.create(world, user, stack, hand)) {
            user.setStackInHand(hand, ItemStack.EMPTY);
            if (user instanceof ServerPlayerEntity player) {
                TriggerCriterion.trigger(player, USE_TRIGGER);
            }
            return TypedActionResult.success(stack);
        }

        return super.use(world, user, hand);
    }

    @Override
    public int getEnchantability() {
        return 8;
    }

    public void tickGlider(ServerWorld world, GliderEntity entity, Entity passenger, ItemStack itemStack) {
        if (entity.age % 2 == 0) {
            var effect = itemStack.get(GlideDataComponents.PARTICLE_EFFECT);
            if (effect != null) {
                world.spawnParticles(effect, entity.getX(), entity.getY() + 1.5, entity.getZ(), Math.max((int) (entity.getVelocity().lengthSquared() * 3f), 2), 0.8, 0, 0.8, 0);
            }
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.MUSIC_DISC_CHIRP;
    }
}
