package eu.pb4.glideaway.util;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

import static eu.pb4.glideaway.ModInit.id;

public class GlideSoundEvents {
    public static final SoundEvent HANG_GLIDER_OPENS = SoundEvent.of(id("item.hang_glider.opens"));
    public static final RegistryEntry<SoundEvent> WIND = RegistryEntry.of(SoundEvent.of(id("generic.wind")));
}
