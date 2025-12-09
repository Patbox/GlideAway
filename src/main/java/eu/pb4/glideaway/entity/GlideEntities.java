package eu.pb4.glideaway.entity;

import eu.pb4.glideaway.ModInit;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class GlideEntities {
    public static final EntityType<GliderEntity> GLIDER
            = register("hang_glider", EntityType.Builder.of(GliderEntity::new, MobCategory.MISC)
            .clientTrackingRange(5).updateInterval(1).sized(0.8f, 1.3f));

    public static void register() {
        GliderEntity.FLY_WITH_GLIDER.codec();
    }

    public static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> item) {
        var id = Identifier.fromNamespaceAndPath(ModInit.ID, path);
        var x = Registry.register(BuiltInRegistries.ENTITY_TYPE, id, item.build(ResourceKey.create(Registries.ENTITY_TYPE, id)));
        PolymerEntityUtils.registerType(x);
        return x;
    }
}
