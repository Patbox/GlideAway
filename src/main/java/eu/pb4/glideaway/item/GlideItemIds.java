package eu.pb4.glideaway.item;

import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class GlideItemIds {

    public static final ResourceKey<Item> WIND_IN_A_BOTTLE = register("wind_in_a_bottle");
    public static final ResourceKey<Item> INFINITE_WIND_IN_A_BOTTLE = register("infinite_wind_in_a_bottle");
    public static final ResourceKey<Item> HANG_GLIDER = register("hang_glider");
    public static final ResourceKey<Item> CHERRY_HANG_GLIDER = register("cherry_hang_glider");
    public static final ResourceKey<Item> SCULK_HANG_GLIDER = register("sculk_hang_glider");
    public static final ResourceKey<Item> AZALEA_HANG_GLIDER = register("azalea_hang_glider");
    public static final ResourceKey<Item> TATER_HANG_GLIDER = register("tater_hang_glider");
    public static final ResourceKey<Item> PHANTOM_HANG_GLIDER = register("phantom_hang_glider");

    public static ResourceKey<Item> register(String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ModInit.ID, path));
    }
}
