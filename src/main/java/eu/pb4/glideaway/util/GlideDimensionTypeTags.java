package eu.pb4.glideaway.util;

import eu.pb4.glideaway.ModInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class GlideDimensionTypeTags {
    public static final TagKey<DimensionType> VOID_PICKUP = of("pick_hang_glider_in_void");
    public static final TagKey<DimensionType> LOW_GRAVITY = of("low_gravity");
    public static final TagKey<DimensionType> HIGH_GRAVITY = of("high_gravity");

    private static TagKey<DimensionType> of(String path) {
        return TagKey.create(Registries.DIMENSION_TYPE, ModInit.id(path));
    }

    public static boolean isIn(Level world, TagKey<DimensionType> tag) {
        return world.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).wrapAsHolder(world.dimensionType()).is(tag);
    }
}
