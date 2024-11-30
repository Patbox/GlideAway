package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.glideaway.mixin.ShapedRecipeAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static eu.pb4.glideaway.ModInit.id;

class RecipesProvider extends FabricRecipeProvider {
    public RecipesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                //noinspection unchecked
                var item = registryLookup.getOrThrow(RegistryKeys.ITEM);

                ShapelessRecipeJsonBuilder.create(item, RecipeCategory.TOOLS, GlideItems.WIND_IN_A_BOTTLE, 1)
                        .input(Items.GLASS_BOTTLE)
                        .input(Items.WIND_CHARGE)
                        .criterion("wool", InventoryChangedCriterion.Conditions.items(Items.WIND_CHARGE))
                        .offerTo(exporter);

                for (var dye : DyeColor.values()) {
                    var wool = Registries.ITEM.get(Identifier.ofVanilla(dye.asString() + "_wool"));
                    var color = dye.getEntityColor();

                    var stack = new ItemStack(GlideItems.HANG_GLIDER);
                    stack.set(DataComponentTypes.DYED_COLOR, dye != DyeColor.WHITE ? new DyedColorComponent(color, true) : null);

                    var b = ShapedRecipeJsonBuilder.create(item, RecipeCategory.TOOLS, stack.getItem(), 1)
                            .group("intotheskies:glider")
                            .pattern("pww")
                            .pattern("ipw")
                            .pattern(" ip")
                            .input('i', Items.STICK)
                            .input('w', wool)
                            .input('p', Items.PHANTOM_MEMBRANE)
                            .criterion("membrane", InventoryChangedCriterion.Conditions.items(Items.PHANTOM_MEMBRANE))
                            .criterion("wool", InventoryChangedCriterion.Conditions.items(wool));

                    b.offerTo(new RecipeExporter() {
                        @Override
                        public void accept(RegistryKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
                            var base = ((ShapedRecipe) recipe);
                            exporter.accept(key, new ShapedRecipe(base.getGroup(), base.getCategory(), ((ShapedRecipeAccessor) base).getRaw(), stack), advancement);
                        }

                        @Override
                        public Advancement.Builder getAdvancementBuilder() {
                            return exporter.getAdvancementBuilder();
                        }

                        @Override
                        public void addRootAdvancement() {
                            exporter.addRootAdvancement();
                        }
                    },  "glider/" + dye.asString());
                }
            }

            public void of(RecipeExporter exporter, RecipeEntry<?>... recipes) {
                for (var recipe : recipes) {
                    exporter.accept(recipe.id(), recipe.value(), null);
                }
            }
        };
    }

    @Override
    public String getName() {
        return "recipe";
    }
}
