package eu.pb4.glideaway.datagen;

import eu.pb4.factorytools.api.recipe.NbtRecipeBuilder;
import eu.pb4.glideaway.item.GlideItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static eu.pb4.glideaway.ModInit.id;


class RecipesProvider extends FabricRecipeProvider {
    public RecipesProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        //noinspection unchecked

        for (var dye : DyeColor.values()) {
            var wool = Registries.ITEM.get(new Identifier(dye.asString() + "_wool"));

            var b = new ShapedRecipeJsonBuilder(RecipeCategory.TOOLS, GlideItems.HANG_GLIDER, 1)
                    .group("intotheskies:glider")
                    .pattern("pww")
                    .pattern("ipw")
                    .pattern(" ip")
                    .input('i', Items.STICK)
                    .input('w', wool)
                    .input('p', Items.PHANTOM_MEMBRANE)
                    .criterion("membrane", InventoryChangedCriterion.Conditions.items(Items.PHANTOM_MEMBRANE));

            if (dye != DyeColor.WHITE) {
                b.criterion("wool", InventoryChangedCriterion.Conditions.items(wool));

                var nbt = new NbtCompound();
                var display = new NbtCompound();
                var c = dye.getColorComponents();
                var color = MathHelper.packRgb(c[0], c[1], c[2]);
                display.putInt(ItemStack.COLOR_KEY, color);
                nbt.put(ItemStack.DISPLAY_KEY, display);

                ((NbtRecipeBuilder) b).factorytools$setNbt(nbt);
            }

            b.offerTo(exporter, id("glider/" + dye.asString()));
        }
    }
    public void of(RecipeExporter exporter, RecipeEntry<?>... recipes) {
        for (var recipe : recipes) {
            exporter.accept(recipe.id(), recipe.value(), null);
        }
    }
}
