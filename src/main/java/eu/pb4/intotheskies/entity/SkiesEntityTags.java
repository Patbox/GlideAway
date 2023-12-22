package eu.pb4.intotheskies.entity;

import eu.pb4.intotheskies.ModInit;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SkiesEntityTags {

    private static TagKey<EntityType<?>> of(String path) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, ModInit.id(path));
    }}
