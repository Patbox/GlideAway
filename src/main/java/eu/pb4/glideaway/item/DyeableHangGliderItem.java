package eu.pb4.glideaway.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class DyeableHangGliderItem extends HangGliderItem {
    public DyeableHangGliderItem(Settings settings) {
        super(Items.LEATHER_LEGGINGS, settings);
    }

    public int getColor(ItemStack stack) {
        var dye = stack.get(DataComponentTypes.DYED_COLOR);
        return dye != null ? dye.rgb() : 0xffffff;
    }

    @Override
    public int getPolymerArmorColor(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return getColor(itemStack);
    }
}
