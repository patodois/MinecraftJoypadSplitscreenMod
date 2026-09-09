import com.shiny.joypadmod.ControllerSettings;
import com.shiny.joypadmod.minecraftExtensions.JoypadMouseHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

/** Runs the production mouse helper with headless Minecraft/LWJGL fixtures.
 * Checks transitions and side effects; does not substitute for an OS cursor test. */
public class CursorCaptureTest {
    static int checks;
    static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
        checks++;
    }
    public static void main(String[] args) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.mouseHelper = new JoypadMouseHelper();
        mc.mouseHelper.deltaX = 20; mc.mouseHelper.deltaY = 30;
        JoypadMouseHelper.updateCapture();
        check(Mouse.grabbed, "focused gameplay hides/captures cursor without a mouse click");
        check(Mouse.dx == 0 && Mouse.dy == 0 && mc.mouseHelper.deltaX == 0 && mc.mouseHelper.deltaY == 0,
            "discard camera warp deltas on capture");
        int changes = Mouse.changes;
        for (int i=0;i<100;i++) JoypadMouseHelper.updateCapture();
        check(Mouse.changes == changes, "stable gameplay does not repeatedly warp the mouse");
        mc.currentScreen = new Object(); JoypadMouseHelper.updateCapture();
        check(!Mouse.grabbed, "inventory/menu releases cursor");
        mc.currentScreen = null; JoypadMouseHelper.updateCapture();
        check(Mouse.grabbed, "closing menu captures again");
        Display.active = false; JoypadMouseHelper.updateCapture();
        check(!Mouse.grabbed, "switching to another application releases capture");
        mc.mouseHelper.grabMouseCursor();
        check(!Mouse.grabbed, "controller does not recapture an unfocused window");
        Display.active = true; JoypadMouseHelper.updateCapture();
        check(Mouse.grabbed, "returning to game captures again");
        ControllerSettings.grabMouse = false; JoypadMouseHelper.updateCapture();
        check(!Mouse.grabbed, "explicit split-screen opt-out releases capture");
        mc.mouseHelper.grabMouseCursor();
        check(!Mouse.grabbed, "vanilla callback respects opt-out");
        Mouse.grabbed = true; mc.mouseHelper.ungrabMouseCursor();
        check(!Mouse.grabbed, "release callback always works even after opting out");
        ControllerSettings.grabMouse = true;
        mc.theWorld = null; JoypadMouseHelper.updateCapture();
        check(!Mouse.grabbed, "main menu is not captured");
        mc.theWorld = new Object(); mc.thePlayer = null; JoypadMouseHelper.updateCapture();
        check(!Mouse.grabbed, "loading world is not captured");
        mc.thePlayer = new Object(); ControllerSettings.suspended = true; JoypadMouseHelper.updateCapture();
        check(!Mouse.grabbed, "calibration or suspended controller input releases cursor");
        ControllerSettings.suspended = false; ControllerSettings.modDisabled = true; JoypadMouseHelper.updateCapture();
        check(!Mouse.grabbed, "disabled mod does not capture");
        ControllerSettings.modDisabled = false; ControllerSettings.enabled = false;
        Mouse.grabbed = true; changes = Mouse.changes; JoypadMouseHelper.updateCapture();
        check(Mouse.grabbed && Mouse.changes == changes, "keyboard/mouse mode stays under vanilla control");
        mc.mouseHelper.ungrabMouseCursor();
        check(!Mouse.grabbed, "vanilla mouse release still works");
        mc.mouseHelper.grabMouseCursor();
        check(Mouse.grabbed, "vanilla mouse capture still works");
        ControllerSettings.enabled = true; Mouse.created = false; changes = Mouse.changes;
        JoypadMouseHelper.updateCapture();
        check(Mouse.changes == changes, "no native access before Mouse initialization");
        System.out.println("PASS: " + checks + " cursor capture assertions (headless fixtures).");
    }
}
