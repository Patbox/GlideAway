package eu.pb4.glideaway.item;

import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static eu.pb4.glideaway.ModInit.id;

public class GlideItems {

    public static final WindInABottleItem WIND_IN_A_BOTTLE = register("wind_in_a_bottle", (settings) -> new WindInABottleItem(settings.stacksTo(8), true));
    public static final WindInABottleItem INFINITE_WIND_IN_A_BOTTLE = register("infinite_wind_in_a_bottle", (settings) -> new WindInABottleItem(settings.stacksTo(1), false));
    public static final HangGliderItem HANG_GLIDER = register("hang_glider", gliderSettings(300, null), HangGliderItem::new);
    public static final HangGliderItem CHERRY_HANG_GLIDER = register("cherry_hang_glider", gliderSettings(400, ParticleTypes.CHERRY_LEAVES), HangGliderItem::new);
    public static final HangGliderItem SCULK_HANG_GLIDER = register("sculk_hang_glider", gliderSettings(400, ParticleTypes.SCULK_CHARGE_POP), HangGliderItem::new);
    public static final HangGliderItem AZALEA_HANG_GLIDER = register("azalea_hang_glider", gliderSettings(400, ParticleTypes.SPORE_BLOSSOM_AIR), HangGliderItem::new);
    public static final HangGliderItem TATER_HANG_GLIDER = register("tater_hang_glider", gliderSettings(-1, null).rarity(Rarity.EPIC), HangGliderItem::new);
    public static final HangGliderItem PHANTOM_HANG_GLIDER = register("phantom_hang_glider", gliderSettings(400, ParticleTypes.MYCELIUM), HangGliderItem::new);


    private static Item.Properties gliderSettings(int damage, @Nullable ParticleOptions particleEffect) {
        var x = new Item.Properties().enchantable(8).repairable(Items.PHANTOM_MEMBRANE)
                .rarity(particleEffect != null ? Rarity.UNCOMMON : Rarity.COMMON)
                .component(GlideDataComponents.PARTICLE_EFFECT, particleEffect);
        if (damage > 0) {
            x.durability(damage);
        }
        return x;
    }

    public static void register() {
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.fromNamespaceAndPath(ModInit.ID, "a_group"), CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, -1)
                .icon(HANG_GLIDER::getDefaultInstance)
                .title(Component.translatable("itemgroup." + ModInit.ID))
                .displayItems(((context, entries) -> {
                    entries.accept(HANG_GLIDER);
                    for (var color : DyeColor.values()) {
                        if (color != DyeColor.WHITE) {
                            var glider = HANG_GLIDER.getDefaultInstance();
                            glider.set(DataComponents.DYED_COLOR, new DyedItemColor(color.getTextureDiffuseColor()));
                            entries.accept(glider);
                        }
                    }
                    entries.accept(CHERRY_HANG_GLIDER);
                    entries.accept(SCULK_HANG_GLIDER);
                    entries.accept(AZALEA_HANG_GLIDER);
                    entries.accept(PHANTOM_HANG_GLIDER);

                    entries.accept(WIND_IN_A_BOTTLE);
                    entries.accept(INFINITE_WIND_IN_A_BOTTLE);
                })).build()
        );

        UseEntityCallback.EVENT.register(WIND_IN_A_BOTTLE::useOnEntityEvent);

        TradeOfferHelper.registerWanderingTraderOffers((b) -> {
            b.pool(id("hang_gliders"), 1,
                    new VillagerTrades.ItemsForEmeralds(GlideItems.AZALEA_HANG_GLIDER, 16, 1, 1, 5),
                    new VillagerTrades.ItemsForEmeralds(GlideItems.CHERRY_HANG_GLIDER, 16, 1, 1, 5),
                    new VillagerTrades.ItemsForEmeralds(GlideItems.SCULK_HANG_GLIDER, 16, 1, 1, 5),
                    new VillagerTrades.ItemsForEmeralds(GlideItems.PHANTOM_HANG_GLIDER, 16, 1, 1, 5)
            );
        });
    }

    public static <T extends Item> T register(String path, Item.Properties settings, Function<Item.Properties, T> function) {
        var id = Identifier.fromNamespaceAndPath(ModInit.ID, path);
        var item = function.apply(settings.setId(ResourceKey.create(Registries.ITEM, id)));
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return item;
    }

    public static <T extends Item> T register(String path, Function<Item.Properties, T> function) {
        return register(path, new Item.Properties(), function);
    }
}
