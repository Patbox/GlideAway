package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.item.GlideItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.CompletableFuture;

import static eu.pb4.glideaway.ModInit.id;


class RecipesProvider extends FabricRecipeProvider {
    public RecipesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        //noinspection unchecked

        new ShapelessRecipeJsonBuilder(RecipeCategory.TOOLS, GlideItems.WIND_IN_A_BOTTLE, 1)
                .input(Items.GLASS_BOTTLE)
                .input(Items.WIND_CHARGE)
                .criterion("wool", InventoryChangedCriterion.Conditions.items(Items.WIND_CHARGE))
                .offerTo(exporter);

        for (var dye : DyeColor.values()) {
            var wool = Registries.ITEM.get(Identifier.ofVanilla(dye.asString() + "_wool"));
            var color = dye.getEntityColor();

            var b = new CompShapedRecipeJsonBuilder(RecipeCategory.TOOLS, GlideItems.HANG_GLIDER, 1)
                    .setComponent(DataComponentTypes.DYED_COLOR, dye != DyeColor.WHITE ? new DyedColorComponent(color, true) : null)
                    .group("intotheskies:glider")
                    .pattern("pww")
                    .pattern("ipw")
                    .pattern(" ip")
                    .input('i', Items.STICK)
                    .input('w', wool)
                    .input('p', Items.PHANTOM_MEMBRANE)
                    .criterion("membrane", InventoryChangedCriterion.Conditions.items(Items.PHANTOM_MEMBRANE))
                    .criterion("wool", InventoryChangedCriterion.Conditions.items(wool));

            b.offerTo(exporter, id("glider/" + dye.asString()));
        }
    }
    public void of(RecipeExporter exporter, RecipeEntry<?>... recipes) {
        for (var recipe : recipes) {
            exporter.accept(recipe.id(), recipe.value(), null);
        }
    }
}
