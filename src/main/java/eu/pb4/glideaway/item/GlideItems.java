package eu.pb4.glideaway.item;

import eu.pb4.factorytools.api.block.MultiBlock;
import eu.pb4.factorytools.api.item.FactoryBlockItem;
import eu.pb4.factorytools.api.item.MultiBlockItem;
import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.block.Block;
import net.minecraft.block.WoodType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GlideItems {

    public static final DyeableHangGliderItem HANG_GLIDER = register("hang_glider", new DyeableHangGliderItem(new Item.Settings().maxDamage(200)));
    public static final ParticleHangGliderItem CHERRY_HANG_GLIDER = register("cherry_hang_glider", new ParticleHangGliderItem(new Item.Settings().maxDamage(300), ParticleTypes.CHERRY_LEAVES));

    public static void register() {
        PolymerItemGroupUtils.registerPolymerItemGroup(new Identifier(ModInit.ID, "a_group"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(HANG_GLIDER::getDefaultStack)
                .displayName(Text.translatable("itemgroup." + ModInit.ID))
                .entries(((context, entries) -> {
                    entries.add(HANG_GLIDER);
                    for (var color : DyeColor.values()) {
                        if (color != DyeColor.WHITE) {
                            var glider = HANG_GLIDER.getDefaultStack();
                            var c = color.getColorComponents();
                            HANG_GLIDER.setColor(glider, MathHelper.packRgb(c[0], c[1], c[2]));
                            entries.add(glider);
                        }
                    }
                    entries.add(CHERRY_HANG_GLIDER);
                })).build()
        );
    }

    private static <T extends Item> Map<WoodType, T> registerWood(String id, Function<WoodType, T> object) {
        var map = new HashMap<WoodType, T>();

        WoodType.stream().forEach(x -> {
            var y = object.apply(x);
            if (y != null) {
                map.put(x, register(x.name() + "_" + id, y));
            }
        });

        return map;
    }

    private static <T extends Item> Map<DyeColor, T> registerDye(String id, Function<DyeColor, T> object) {
        var map = new HashMap<DyeColor, T>();

        for (var x : DyeColor.values()) {
            var y = object.apply(x);
            if (y != null) {
                map.put(x, register(x.name() + "_" + id, y));
            }
        }

        return map;
    }

    public static <T extends Item> T register(String path, T item) {
        Registry.register(Registries.ITEM, new Identifier(ModInit.ID, path), item);
        return item;
    }

    public static <E extends Block & PolymerBlock> BlockItem register(E block) {
        var id = Registries.BLOCK.getId(block);
        BlockItem item;
        if (block instanceof MultiBlock multiBlock) {
            item = new MultiBlockItem(multiBlock, new Item.Settings());
        } else {
            item = new FactoryBlockItem(block, new Item.Settings());
        }

        Registry.register(Registries.ITEM, id, item);
        return item;
    }
}
