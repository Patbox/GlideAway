package eu.pb4.glideaway.item;

import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import static eu.pb4.glideaway.ModInit.id;

public class HangGliderItem extends Item implements PolymerItem {

    public HangGliderItem(Settings settings) {
        super(settings);

        DispenserBlock.registerBehavior(this, new ItemDispenserBehavior() {
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                return GliderEntity.createDispenser(pointer, stack);
            }
        });
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        if (world instanceof ServerWorld serverWorld && GliderEntity.create(serverWorld, user, stack, hand)) {
            user.setStackInHand(hand, ItemStack.EMPTY);
            return ActionResult.SUCCESS_SERVER;
        }

        return super.use(world, user, hand);
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
