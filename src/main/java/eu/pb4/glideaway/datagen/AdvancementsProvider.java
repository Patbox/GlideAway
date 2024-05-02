package eu.pb4.glideaway.datagen;

import eu.pb4.factorytools.api.advancement.TriggerCriterion;
import eu.pb4.glideaway.entity.GliderEntity;
import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.glideaway.item.HangGliderItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


class AdvancementsProvider extends FabricAdvancementProvider {

    protected AdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> exporter) {
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
                .parent(new Identifier("adventure/root"))
                .criterion("any_item", TriggerCriterion.of(HangGliderItem.USE_TRIGGER))
                .build(exporter, "glideaway:into_the_skies");

    }
}
