package com.shiny.joypadmod.minecraftExtensions;

import com.shiny.joypadmod.ControllerSettings;
import com.shiny.joypadmod.helpers.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class JoypadMouseHelper extends MouseHelper {
    @Override public void grabMouseCursor() {
        if (ControllerSettings.isInputEnabled()
                && (!ControllerSettings.grabMouse || !Display.isActive())) return;
        super.grabMouseCursor();
    }

    @Override public void ungrabMouseCursor() {
        // Always honor Minecraft's release on menus or focus loss, even after
        // changing the controller preference while the mouse was grabbed.
        super.ungrabMouseCursor();
    }

    /** Controller input can set inGameHasFocus without calling vanilla's grab.
     * Reconcile the real LWJGL state, including when returning from a GUI. */
    public static void updateCapture() {
        if (!Mouse.isCreated() || !ControllerSettings.isInputEnabled()) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) return;
        boolean capture = ControllerSettings.grabMouse && !ControllerSettings.modDisabled
                && mc.theWorld != null && mc.thePlayer != null && mc.currentScreen == null
                && Display.isActive() && !ControllerSettings.isSuspended();
        if (Mouse.isGrabbed() == capture) return;
        Mouse.setGrabbed(capture);
        // Drop warp/relative deltas at the boundary to avoid a camera jump.
        Mouse.getDX();
        Mouse.getDY();
        if (mc.mouseHelper != null) {
            mc.mouseHelper.deltaX = 0;
            mc.mouseHelper.deltaY = 0;
        }
        LogHelper.Info(capture ? "Gameplay cursor captured" : "Gameplay cursor released");
    }
}
