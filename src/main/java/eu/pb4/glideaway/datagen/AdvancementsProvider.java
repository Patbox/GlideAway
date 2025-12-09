package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.item.GlideItemTags;
import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.glideaway.item.HangGliderItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.DistanceTrigger;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


class AdvancementsProvider extends FabricAdvancementProvider {

    protected AdvancementsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> exporter) {
        var item = registryLookup.lookupOrThrow(Registries.ITEM);
        //noinspection removal
        var root = Advancement.Builder.advancement()
                .display(
                        GlideItems.HANG_GLIDER,
                        Component.translatable("advancements.glideaway.into_the_skies.title"),
                        Component.translatable("advancements.glideaway.into_the_skies.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .parent(Identifier.withDefaultNamespace("adventure/root"))
                .addCriterion("any_item", GliderEntity.FLY_WITH_GLIDER.createCriterion(new DistanceTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(DistancePredicate.absolute(MinMaxBounds.Doubles.atLeast(5))))))
                .save(exporter, "glideaway:into_the_skies");

    }
}
