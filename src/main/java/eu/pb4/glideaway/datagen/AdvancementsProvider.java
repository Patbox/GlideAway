package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.item.GlideItemTags;
import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.glideaway.item.HangGliderItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.advancement.criterion.UsingItemCriterion;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


class AdvancementsProvider extends FabricAdvancementProvider {

    protected AdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> exporter) {
        var item = registryLookup.getOrThrow(RegistryKeys.ITEM);
        //noinspection removal
        var root = Advancement.Builder.create()
                .display(
                        GlideItems.HANG_GLIDER,
                        Text.translatable("advancements.glideaway.into_the_skies.title"),
                        Text.translatable("advancements.glideaway.into_the_skies.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .parent(Identifier.ofVanilla("adventure/root"))
                .criterion("any_item", GliderEntity.FLY_WITH_GLIDER.create(new TravelCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(DistancePredicate.absolute(NumberRange.DoubleRange.atLeast(5))))))
                .build(exporter, "glideaway:into_the_skies");

    }
}
