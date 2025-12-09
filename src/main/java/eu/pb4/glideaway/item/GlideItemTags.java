package eu.pb4.glideaway.item;

import eu.pb4.glideaway.ModInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class GlideItemTags {
    public static final TagKey<Item> HANG_GLIDERS = of("hand_gliders");
    public static final TagKey<Item> SPECIAL_HANG_GLIDERS = of("special_hand_gliders");
    public static final TagKey<Item> GLIDER_ENCHANTABLE = of("enchantable/glider");

    private static TagKey<Item> of(String path) {
        return TagKey.create(Registries.ITEM, ModInit.id(path));
    }
}
