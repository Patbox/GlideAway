package eu.pb4.glideaway.mixin;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.item.crafting.NormalCraftingRecipe.class)
public interface NormalCraftingRecipeAccessor {
    @Accessor
    Recipe.CommonInfo getCommonInfo();

    @Accessor
    CraftingRecipe.CraftingBookInfo getBookInfo();
}
