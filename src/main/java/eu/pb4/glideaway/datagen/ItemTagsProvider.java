package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.item.GlideItemTags;
import eu.pb4.glideaway.item.GlideItemIds;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    public ItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, @Nullable FabricTagsProvider.BlockTagsProvider blockTagProvider) {
        super(output, registriesFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(GlideItemTags.HANG_GLIDERS)
                .add(GlideItemIds.HANG_GLIDER)
                .addOptionalTag(GlideItemTags.SPECIAL_HANG_GLIDERS);

        this.tag(GlideItemTags.SPECIAL_HANG_GLIDERS)
                .add(GlideItemIds.AZALEA_HANG_GLIDER, GlideItemIds.CHERRY_HANG_GLIDER, GlideItemIds.SCULK_HANG_GLIDER,
                        GlideItemIds.PHANTOM_HANG_GLIDER, GlideItemIds.TATER_HANG_GLIDER);

        this.tag(ItemTags.CAULDRON_CAN_REMOVE_DYE).add(GlideItemIds.HANG_GLIDER);

        this.tag(ItemTags.DURABILITY_ENCHANTABLE)
                .addTag(GlideItemTags.HANG_GLIDERS);
        this.tag(ItemTags.VANISHING_ENCHANTABLE)
                .addTag(GlideItemTags.HANG_GLIDERS);
        this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .addTag(GlideItemTags.HANG_GLIDERS);
        this.tag(GlideItemTags.GLIDER_ENCHANTABLE)
                .addTag(GlideItemTags.HANG_GLIDERS);
    }
}
