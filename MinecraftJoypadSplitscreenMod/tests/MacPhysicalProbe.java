import com.shiny.joypadmod.devices.*;
import java.util.Arrays;

/** Uses the real bundled JNI bridge and real connected controllers. */
public class MacPhysicalProbe {
    public static void main(String[] args) throws Exception {
        final int seconds = args.length == 0 ? 5 : Integer.parseInt(args[0]);
        // Minecraft also runs its client loop on a Java thread, not Cocoa's main thread.
        Thread client = new Thread(new Runnable() { public void run() {
            try {
                MacGamepadLibrary library = new MacGamepadLibrary();
                library.create();
                String last = "";
                long deadline = System.currentTimeMillis() + seconds * 1000L;
                boolean detected = false;
                while (System.currentTimeMillis() < deadline) {
                    library.poll();
                    InputDevice d = library.getCurrentController();
                    boolean[] buttons = new boolean[15];
                    float[] axes = new float[6];
                    for (int i = 0; i < buttons.length; i++) buttons[i] = d.isButtonPressed(i);
                    for (int i = 0; i < axes.length; i++) axes[i] = d.getAxisValue(i);
                    String reading = d.getName() + " connected=" + d.isConnected() + " buttons=" + Arrays.toString(buttons) + " axes=" + Arrays.toString(axes);
                    if (!reading.equals(last)) { System.out.println(reading); last = reading; }
                    detected |= d.isConnected();
                    library.clearEvents();
                    Thread.sleep(15);
                }
                if (!detected) throw new AssertionError("No physical GameController device detected");
                System.out.println("PASS: native GameController device detected through Java/JNI on client thread.");
            } catch (Throwable ex) { ex.printStackTrace(); System.exit(1); }
        }}, "Client thread");
        client.start(); client.join();
        System.exit(0);
    }
}
