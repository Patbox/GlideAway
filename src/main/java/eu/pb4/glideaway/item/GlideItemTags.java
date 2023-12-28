package eu.pb4.glideaway.item;

import eu.pb4.glideaway.ModInit;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class GlideItemTags {
    public static final TagKey<Item> HANG_GLIDERS = of("hand_gliders");
    public static final TagKey<Item> SPECIAL_HANG_GLIDERS = of("special_hand_gliders");
    private static TagKey<Item> of(String path) {
        return TagKey.of(RegistryKeys.ITEM, ModInit.id(path));
    }
}
