package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.util.GlideDimensionTypeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import java.util.concurrent.CompletableFuture;

class DimensionTypeTagsProvider extends FabricTagsProvider<DimensionType> {

    public DimensionTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DIMENSION_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.getOrCreateRawBuilder(GlideDimensionTypeTags.VOID_PICKUP)
                .addOptionalElement(Identifier.fromNamespaceAndPath("cerulean", "skies")
                );

        this.getOrCreateRawBuilder(GlideDimensionTypeTags.LOW_GRAVITY)
                .addOptionalElement(Identifier.fromNamespaceAndPath("cerulean", "skies")
                );

        this.getOrCreateRawBuilder(GlideDimensionTypeTags.HIGH_GRAVITY)
                .addOptionalElement(BuiltinDimensionTypes.END.identifier())
        ;
    }
}
