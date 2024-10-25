package eu.pb4.glideaway.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class DyeableHangGliderItem extends HangGliderItem {
    public DyeableHangGliderItem(Settings settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.WOLF_ARMOR;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        var out = super.getPolymerItemStack(itemStack, tooltipType, context);
        if (!out.contains(DataComponentTypes.DYED_COLOR)) {
            out.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFFFFFF, false));
        }
        return out;
    }
}
