package eu.pb4.glideaway.util;

import static eu.pb4.glideaway.ModInit.id;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class GlideSoundEvents {
    public static final SoundEvent HANG_GLIDER_OPENS = SoundEvent.createVariableRangeEvent(id("item.hang_glider.opens"));
    public static final Holder<SoundEvent> WIND = Holder.direct(SoundEvent.createVariableRangeEvent(id("generic.wind")));
}
