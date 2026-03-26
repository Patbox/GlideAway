package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.item.GlideItemTags;
import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.glideaway.item.GlideVillagerTrades;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.VillagerTradeTags;
import net.minecraft.world.item.trading.VillagerTrade;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class VillagerTradeTagsProvider extends FabricTagsProvider<VillagerTrade> {
    public VillagerTradeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.VILLAGER_TRADE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.builder(VillagerTradeTags.WANDERING_TRADER_UNCOMMON)
                .add(GlideVillagerTrades.HANG_GLIDERS_AZALEA)
                .add(GlideVillagerTrades.HANG_GLIDERS_CHERRY)
                .add(GlideVillagerTrades.HANG_GLIDERS_PHANTOM)
                .add(GlideVillagerTrades.HANG_GLIDERS_SCULK)
        ;
    }
}
