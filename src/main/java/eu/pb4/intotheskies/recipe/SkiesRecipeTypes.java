package eu.pb4.intotheskies.recipe;

import eu.pb4.intotheskies.ModInit;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkiesRecipeTypes {


    public static void register() {

    }

    public static <T extends Recipe<?>> RecipeType<T> register(String path) {
        return Registry.register(Registries.RECIPE_TYPE, new Identifier(ModInit.ID, path), new RecipeType<T>() {
            @Override
            public String toString() {
                return ModInit.ID + ":" + path;
            }
        });
    }
}
