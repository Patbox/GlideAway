package eu.pb4.intotheskies.ui;

import eu.pb4.sgui.api.elements.GuiElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class GuiUtils {
    public static final GuiElement EMPTY = GuiElement.EMPTY;

    public static final void playClickSound(ServerPlayerEntity player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.5f, 1);
    }
}

