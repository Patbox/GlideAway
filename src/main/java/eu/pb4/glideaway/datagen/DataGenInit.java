package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.item.GlideVillagerTrades;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class DataGenInit implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();

        var blockTags = pack.addProvider(BlockTagsProvider::new);
        pack.addProvider((a, b) -> new ItemTagsProvider(a, b, blockTags));
        pack.addProvider(EntityTagsProvider::new);
        pack.addProvider(DimensionTypeTagsProvider::new);
        pack.addProvider(CustomRegistryProvider::new);
        pack.addProvider(RecipesProvider::new);
        pack.addProvider(AdvancementsProvider::new);
        pack.addProvider(CustomAssetProvider::new);
        pack.addProvider(VillagerTradeTagsProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.VILLAGER_TRADE, GlideVillagerTrades::bootstrap);
    }
}
