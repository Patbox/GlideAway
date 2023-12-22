package eu.pb4.intotheskies.item;

import eu.pb4.factorytools.api.item.ModeledItem;
import eu.pb4.intotheskies.entity.GliderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GliderItem extends ModeledItem {
    public GliderItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        if (GliderEntity.create(world, user, stack)) {
            user.setStackInHand(hand, ItemStack.EMPTY);
            return TypedActionResult.success(stack);
        }

        return super.use(world, user, hand);
    }
}
