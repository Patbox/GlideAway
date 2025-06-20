package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.item.GlideItemTags;
import eu.pb4.glideaway.item.GlideItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.valueLookupBuilder(GlideItemTags.HANG_GLIDERS)
                .add(GlideItems.HANG_GLIDER)
                .addOptionalTag(GlideItemTags.SPECIAL_HANG_GLIDERS);

        this.valueLookupBuilder(GlideItemTags.SPECIAL_HANG_GLIDERS)
                .add(GlideItems.AZALEA_HANG_GLIDER, GlideItems.CHERRY_HANG_GLIDER, GlideItems.SCULK_HANG_GLIDER,
                        GlideItems.PHANTOM_HANG_GLIDER, GlideItems.TATER_HANG_GLIDER);

        this.valueLookupBuilder(ItemTags.DYEABLE).add(GlideItems.HANG_GLIDER);

        this.valueLookupBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .addTag(GlideItemTags.HANG_GLIDERS);
        this.valueLookupBuilder(ItemTags.VANISHING_ENCHANTABLE)
                .addTag(GlideItemTags.HANG_GLIDERS);
        this.valueLookupBuilder(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .addTag(GlideItemTags.HANG_GLIDERS);
        this.valueLookupBuilder(GlideItemTags.GLIDER_ENCHANTABLE)
                .addTag(GlideItemTags.HANG_GLIDERS);
    }
}
