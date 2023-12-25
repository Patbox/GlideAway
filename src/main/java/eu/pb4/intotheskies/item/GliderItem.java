package eu.pb4.intotheskies.item;

import eu.pb4.factorytools.api.item.ModeledItem;
import eu.pb4.factorytools.api.resourcepack.BaseItemProvider;
import eu.pb4.intotheskies.entity.GliderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GliderItem extends ModeledItem implements DyeableItem {
    public GliderItem(Settings settings) {
        super(Items.LEATHER_LEGGINGS, settings);
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

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : 0xffffff;
    }

    @Override
    public int getPolymerArmorColor(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return getColor(itemStack);
    }
}
