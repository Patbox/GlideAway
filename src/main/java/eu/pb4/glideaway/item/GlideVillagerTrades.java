package eu.pb4.glideaway.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.Optional;

import static eu.pb4.glideaway.ModInit.id;

public class GlideVillagerTrades {
    public static final ResourceKey<VillagerTrade> HANG_GLIDERS_AZALEA = key("hang_gliders/azalea");
    public static final ResourceKey<VillagerTrade> HANG_GLIDERS_CHERRY = key("hang_gliders/cherry");
    public static final ResourceKey<VillagerTrade> HANG_GLIDERS_SCULK = key("hang_gliders/sculk");
    public static final ResourceKey<VillagerTrade> HANG_GLIDERS_PHANTOM = key("hang_gliders/phantom");

    private static ResourceKey<VillagerTrade> key(String s) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, id(s));
    }

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        /*TradeOfferHelper.registerWanderingTraderOffers((b) -> {
            b.pool(id("hang_gliders"), 1,
                    new VillagerTrades.ItemsForEmeralds(GlideItems.AZALEA_HANG_GLIDER, 16, 1, 1, 5),
                    new VillagerTrades.ItemsForEmeralds(GlideItems.CHERRY_HANG_GLIDER, 16, 1, 1, 5),
                    new VillagerTrades.ItemsForEmeralds(GlideItems.SCULK_HANG_GLIDER, 16, 1, 1, 5),
                    new VillagerTrades.ItemsForEmeralds(GlideItems.PHANTOM_HANG_GLIDER, 16, 1, 1, 5)
            );
        });*/


        TradeCost wants = new TradeCost(Items.EMERALD, new UniformGenerator(new ConstantValue(14), new ConstantValue(16)));

        context.register(HANG_GLIDERS_AZALEA, new VillagerTrade(
                wants,
                new ItemStackTemplate(GlideItems.AZALEA_HANG_GLIDER), 1, 5, 0.2f, Optional.empty(), List.of()
        ));
        context.register(HANG_GLIDERS_CHERRY, new VillagerTrade(
                wants,
                new ItemStackTemplate(GlideItems.CHERRY_HANG_GLIDER), 1, 5, 0.2f, Optional.empty(), List.of()
        ));
        context.register(HANG_GLIDERS_SCULK, new VillagerTrade(
                wants,
                new ItemStackTemplate(GlideItems.SCULK_HANG_GLIDER), 1, 5, 0.2f, Optional.empty(), List.of()
        ));
        context.register(HANG_GLIDERS_PHANTOM, new VillagerTrade(
                wants,
                new ItemStackTemplate(GlideItems.PHANTOM_HANG_GLIDER), 1, 5, 0.2f, Optional.empty(), List.of()
        ));
    }
}
