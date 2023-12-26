package eu.pb4.glideaway.item;

import eu.pb4.factorytools.api.item.ModeledItem;
import eu.pb4.glideaway.entity.GlideEntities;
import eu.pb4.glideaway.entity.GliderEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HangGliderItem extends ModeledItem {
    public HangGliderItem(Item item, Settings settings) {
        super(item, settings);

        DispenserBlock.registerBehavior(this, new ItemDispenserBehavior() {
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction direction = pointer.state().get(DispenserBlock.FACING);
                ServerWorld serverWorld = pointer.world();
                Vec3d vec3d = pointer.centerPos();

                var entity = new GliderEntity(GlideEntities.GLIDER, serverWorld);
                entity.setItemStack(stack.copyWithCount(1));
                entity.setPosition(vec3d.x, vec3d.y - 0.5f, vec3d.z);
                entity.setYaw(direction.getAxis() == Direction.Axis.Y ? 0 : direction.asRotation());
                entity.setPitch(direction.getAxis() != Direction.Axis.Y ? 0 : (direction == Direction.UP ? -90 : 90));
                entity.setVelocity(Vec3d.of(direction.getVector()).multiply(0.6));

                serverWorld.spawnEntity(entity);
                stack.decrement(1);
                return stack;
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
            return TypedActionResult.success(stack);
        }

        return super.use(world, user, hand);
    }

    public void tickGlider(ServerWorld world, GliderEntity entity, Entity passenger, ItemStack itemStack) {}
}
