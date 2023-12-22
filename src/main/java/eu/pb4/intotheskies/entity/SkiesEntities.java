package eu.pb4.intotheskies.entity;

import eu.pb4.intotheskies.ModInit;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkiesEntities {
    public static final EntityType<GliderEntity> GLIDER
            = register("glider", FabricEntityTypeBuilder
            .create().dimensions(EntityDimensions.fixed(0f, 0f)).entityFactory(GliderEntity::new).disableSaving().disableSummon());

    public static void register() {

    }

    public static <T extends Entity> EntityType<T> register(String path, FabricEntityTypeBuilder<T> item) {
        var x = Registry.register(Registries.ENTITY_TYPE, new Identifier(ModInit.ID, path), item.build());
        PolymerEntityUtils.registerType(x);
        return x;
    }
}
