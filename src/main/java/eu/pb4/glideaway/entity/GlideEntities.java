package eu.pb4.glideaway.entity;

import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class GlideEntities {
    public static final EntityType<GliderEntity> GLIDER
            = register("hang_glider", EntityType.Builder.create(GliderEntity::new, SpawnGroup.MISC)
            .maxTrackingRange(5).trackingTickInterval(1).dimensions(0.8f, 1.3f));

    public static void register() {
        GliderEntity.FLY_WITH_GLIDER.getConditionsCodec();
    }

    public static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> item) {
        var id = Identifier.of(ModInit.ID, path);
        var x = Registry.register(Registries.ENTITY_TYPE, id, item.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)));
        PolymerEntityUtils.registerType(x);
        return x;
    }
}
