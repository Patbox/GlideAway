package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.util.GlideDimensionTypeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.concurrent.CompletableFuture;

class DimensionTypeTagsProvider extends FabricTagProvider<DimensionType> {

    public DimensionTypeTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DIMENSION_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.getTagBuilder(GlideDimensionTypeTags.VOID_PICKUP)
                .addOptional(Identifier.of("cerulean", "skies")
                );

        this.getTagBuilder(GlideDimensionTypeTags.LOW_GRAVITY)
                .addOptional(Identifier.of("cerulean", "skies")
                );

        this.getTagBuilder(GlideDimensionTypeTags.HIGH_GRAVITY)
                .addOptional(DimensionTypes.THE_END_ID)
        ;
    }
}
