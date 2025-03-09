package eu.pb4.glideaway.item;

import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class GlideDataComponents {
    public static ComponentType<ParticleEffect> PARTICLE_EFFECT = register("particle_effect", ComponentType.<ParticleEffect>builder().codec(ParticleTypes.TYPE_CODEC));


    @SuppressWarnings("unchecked")
    @Nullable
    public static final ComponentType<Identifier> ITEM_MODEL = (ComponentType<Identifier>) Registries.DATA_COMPONENT_TYPE.get(Identifier.ofVanilla("item_model"));

    public static <T> ComponentType<T> register(String path, ComponentType.Builder<T> builder) {
        var x =  Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(ModInit.ID, path), builder.build());
        PolymerComponent.registerDataComponent(x);
        return x;
    }
}
