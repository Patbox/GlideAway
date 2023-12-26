package eu.pb4.glideaway.entity;

import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class GlideEntities {
    public static final EntityType<GliderEntity> GLIDER
            = register("hang_glider", FabricEntityTypeBuilder
            .create().trackedUpdateRate(1).dimensions(EntityDimensions.fixed(0.8f, 1.3f)).entityFactory(GliderEntity::new).disableSummon());

    public static void register() {

    }

    public static <T extends Entity> EntityType<T> register(String path, FabricEntityTypeBuilder<T> item) {
        var x = Registry.register(Registries.ENTITY_TYPE, new Identifier(ModInit.ID, path), item.build());
        PolymerEntityUtils.registerType(x);
        return x;
    }
}
