package eu.pb4.glideaway.util;

import eu.pb4.glideaway.ModInit;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class GlideDimensionTypeTags {
    public static final TagKey<DimensionType> VOID_PICKUP = of("pick_hang_glider_in_void");
    public static final TagKey<DimensionType> LOW_GRAVITY = of("low_gravity");
    public static final TagKey<DimensionType> HIGH_GRAVITY = of("high_gravity");

    private static TagKey<DimensionType> of(String path) {
        return TagKey.of(RegistryKeys.DIMENSION_TYPE, ModInit.id(path));
    }

    public static boolean isIn(World world, TagKey<DimensionType> tag) {
        return world.getRegistryManager().getOrThrow(RegistryKeys.DIMENSION_TYPE).getEntry(world.getDimension()).isIn(tag);
    }
}
