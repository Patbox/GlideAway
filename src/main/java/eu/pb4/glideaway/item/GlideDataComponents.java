package eu.pb4.glideaway.item;

import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class GlideDataComponents {
    public static DataComponentType<ParticleOptions> PARTICLE_EFFECT = register("particle_effect", DataComponentType.<ParticleOptions>builder().persistent(ParticleTypes.CODEC));

    public static <T> DataComponentType<T> register(String path, DataComponentType.Builder<T> builder) {
        var x =  Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(ModInit.ID, path), builder.build());
        PolymerComponent.registerDataComponent(x);
        return x;
    }
}
