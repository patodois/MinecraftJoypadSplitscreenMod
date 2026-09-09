package com.shiny.joypadmod.devices;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/** Own JNI bridge to Apple's GameController framework; no SDL/LWJGL replacement. */
public final class MacGamepadNative {
    private static boolean loaded;
    private MacGamepadNative() {}
    public static synchronized void load() throws IOException {
        if (loaded) return;
        if (!System.getProperty("os.name", "").startsWith("Mac")) throw new IOException("macOS required");
        InputStream input = MacGamepadNative.class.getResourceAsStream("/natives/macos/libjoypadgamecontroller.dylib");
        if (input == null) throw new FileNotFoundException("macOS gamepad library missing from JAR");
        Path directory = Files.createTempDirectory("joypad-gc-");
        Path library = directory.resolve("libjoypadgamecontroller.dylib");
        try {
            Files.copy(input, library);
        } finally { input.close(); }
        directory.toFile().deleteOnExit();
        library.toFile().deleteOnExit();
        System.load(library.toAbsolutePath().toString());
        start();
        loaded = true;
    }
    private static native void start();
    public static native int slotCount();
    public static native String name(int slot);
    /** connected; buttons 0..14; axes 0..5; battery (23 floats total). */
    public static native void read(int slot, float[] output);
}
