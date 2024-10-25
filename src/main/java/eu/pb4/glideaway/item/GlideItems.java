package eu.pb4.glideaway.item;

import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;

import java.util.function.Function;

import static eu.pb4.glideaway.ModInit.id;

public class GlideItems {

    public static final WindInABottleItem WIND_IN_A_BOTTLE = register("wind_in_a_bottle", (settings) -> new WindInABottleItem(settings.maxCount(8), true));
    public static final WindInABottleItem INFINITE_WIND_IN_A_BOTTLE = register("infinite_wind_in_a_bottle", (settings) -> new WindInABottleItem(settings.maxCount(1), false));
    public static final DyeableHangGliderItem HANG_GLIDER = register("hang_glider", (settings) -> new DyeableHangGliderItem(settings.maxDamage(300).enchantable(8).repairable(Items.PHANTOM_MEMBRANE)));
    public static final ParticleHangGliderItem CHERRY_HANG_GLIDER = register("cherry_hang_glider", (settings) -> new ParticleHangGliderItem(settings.maxDamage(400).enchantable(8).repairable(Items.PHANTOM_MEMBRANE), ParticleTypes.CHERRY_LEAVES));
    public static final ParticleHangGliderItem SCULK_HANG_GLIDER = register("sculk_hang_glider", (settings) -> new ParticleHangGliderItem(settings.maxDamage(400).enchantable(8).repairable(Items.PHANTOM_MEMBRANE), ParticleTypes.SCULK_CHARGE_POP));
    public static final ParticleHangGliderItem AZALEA_HANG_GLIDER = register("azalea_hang_glider", (settings) -> new ParticleHangGliderItem(settings.maxDamage(400).enchantable(8).repairable(Items.PHANTOM_MEMBRANE), ParticleTypes.SPORE_BLOSSOM_AIR));

    public static void register() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(ModInit.ID, "a_group"), ItemGroup.create(ItemGroup.Row.BOTTOM, -1)
                .icon(HANG_GLIDER::getDefaultStack)
                .displayName(Text.translatable("itemgroup." + ModInit.ID))
                .entries(((context, entries) -> {
                    entries.add(HANG_GLIDER);
                    for (var color : DyeColor.values()) {
                        if (color != DyeColor.WHITE) {
                            var glider = HANG_GLIDER.getDefaultStack();
                            glider.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color.getEntityColor(), true));
                            entries.add(glider);
                        }
                    }
                    entries.add(CHERRY_HANG_GLIDER);
                    entries.add(SCULK_HANG_GLIDER);
                    entries.add(AZALEA_HANG_GLIDER);

                    entries.add(WIND_IN_A_BOTTLE);
                    entries.add(INFINITE_WIND_IN_A_BOTTLE);
                })).build()
        );

        UseEntityCallback.EVENT.register(WIND_IN_A_BOTTLE::useOnEntityEvent);

        TradeOfferHelper.registerWanderingTraderOffers(2, (b) -> {
            b.add(new TradeOffers.SellItemFactory(GlideItems.AZALEA_HANG_GLIDER, 16, 1, 1, 5));
            b.add(new TradeOffers.SellItemFactory(GlideItems.CHERRY_HANG_GLIDER, 16, 1, 1, 5));
            b.add(new TradeOffers.SellItemFactory(GlideItems.SCULK_HANG_GLIDER, 16, 1, 1, 5));
        });

        TradeOfferHelper.registerRebalancedWanderingTraderOffers((b) -> {
            b.pool(id("hang_gliders"), 1,
                    new TradeOffers.SellItemFactory(GlideItems.AZALEA_HANG_GLIDER, 16, 1, 1, 5),
                    new TradeOffers.SellItemFactory(GlideItems.CHERRY_HANG_GLIDER, 16, 1, 1, 5),
                    new TradeOffers.SellItemFactory(GlideItems.SCULK_HANG_GLIDER, 16, 1, 1, 5)
            );
        });
    }

    public static <T extends Item> T register(String path, Function<Item.Settings, T> function) {
        var id = Identifier.of(ModInit.ID, path);
        var item = function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
        Registry.register(Registries.ITEM, id, item);
        return item;
    }
}
