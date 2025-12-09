package eu.pb4.glideaway.item;

import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import xyz.nucleoid.packettweaker.PacketContext;

import static eu.pb4.glideaway.ModInit.id;

public class HangGliderItem extends Item implements PolymerItem {

    public HangGliderItem(Properties settings) {
        super(settings);

        DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
            public ItemStack execute(BlockSource pointer, ItemStack stack) {
                return GliderEntity.createDispenser(pointer, stack);
            }
        });
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        var stack = user.getItemInHand(hand);
        if (world instanceof ServerLevel serverWorld && GliderEntity.create(serverWorld, user, stack, hand)) {
            user.setItemInHand(hand, ItemStack.EMPTY);
            return InteractionResult.SUCCESS_SERVER;
        }

        return super.use(world, user, hand);
    }

    public void tickGlider(ServerLevel world, GliderEntity entity, Entity passenger, ItemStack itemStack) {
        if (entity.tickCount % 2 == 0) {
            var effect = itemStack.get(GlideDataComponents.PARTICLE_EFFECT);
            if (effect != null) {
                world.sendParticles(effect, entity.getX(), entity.getY() + 1.5, entity.getZ(), Math.max((int) (entity.getDeltaMovement().lengthSqr() * 3f), 2), 0.8, 0, 0.8, 0);
            }
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.MUSIC_DISC_CHIRP;
    }
}
