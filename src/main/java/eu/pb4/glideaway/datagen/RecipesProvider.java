package eu.pb4.glideaway.datagen;

import eu.pb4.glideaway.item.GlideItems;
import eu.pb4.glideaway.mixin.NormalCraftingRecipeAccessor;
import eu.pb4.glideaway.mixin.ShapedRecipeAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static eu.pb4.glideaway.ModInit.id;

class RecipesProvider extends FabricRecipeProvider {
    public RecipesProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                //noinspection unchecked
                var item = registryLookup.lookupOrThrow(Registries.ITEM);

                ShapelessRecipeBuilder.shapeless(item, RecipeCategory.TOOLS, GlideItems.WIND_IN_A_BOTTLE, 1)
                        .requires(Items.GLASS_BOTTLE)
                        .requires(Items.WIND_CHARGE)
                        .unlockedBy("wool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WIND_CHARGE))
                        .save(output);

                for (var dye : DyeColor.values()) {
                    var wool = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(dye.getSerializedName() + "_wool"));
                    var color = dye.getTextureDiffuseColor();

                    var stack = DataComponentPatch.builder();
                    if (dye != DyeColor.WHITE) {
                        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
                    }

                    var b = ShapedRecipeBuilder.shaped(item, RecipeCategory.TOOLS, GlideItems.HANG_GLIDER, 1)
                            .group("intotheskies:glider")
                            .pattern("pww")
                            .pattern("ipw")
                            .pattern(" ip")
                            .define('i', Items.STICK)
                            .define('w', wool)
                            .define('p', Items.PHANTOM_MEMBRANE)
                            .unlockedBy("membrane", InventoryChangeTrigger.TriggerInstance.hasItems(Items.PHANTOM_MEMBRANE))
                            .unlockedBy("wool", InventoryChangeTrigger.TriggerInstance.hasItems(wool));

                    b.save(new RecipeOutput() {
                        @Override
                        public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
                            var base = ((ShapedRecipe) recipe);
                            var nor = (NormalCraftingRecipeAccessor) recipe;
                            output.accept(key, new ShapedRecipe(nor.getCommonInfo(), nor.getBookInfo(), ((ShapedRecipeAccessor) base).getPattern(), new ItemStackTemplate(GlideItems.HANG_GLIDER, stack.build())), advancement);
                        }

                        @Override
                        public Advancement.Builder advancement() {
                            return output.advancement();
                        }

                        @Override
                        public void includeRootAdvancement() {
                            output.includeRootAdvancement();
                        }
                    },  "glideaway:glider/" + dye.getSerializedName());
                }
            }

            public void of(RecipeOutput exporter, RecipeHolder<?>... recipes) {
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
