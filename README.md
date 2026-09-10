# Joypad Enhanced — Minecraft 1.7.10

An improved version of JoypadMod with native **Bluetooth/USB controller support on macOS**, **XInput fixes on Windows**, and a redesigned settings interface.

**[Download version 0.2.1](https://github.com/patodois/MinecraftJoypadSplitscreenMod/releases/tag/v0.2.1)** · **[Build instructions and details](MinecraftJoypadSplitscreenMod/README.md)**

Version **0.2.1** hides and captures the cursor during controller gameplay, releasing it in menus and when switching applications. **Capture mouse** is enabled by default, including when upgrading existing settings.

## Installation

1. Use Minecraft **1.7.10 + Forge + Java 8**.
2. Close the game, remove the old JoypadMod from `mods`, and add the new version's JAR.
3. Open **Options → Controls**, select your device, and open **Test controller**.

On Mac, the controller appears as `macOS - Xbox Wireless Controller` or the corresponding device name. The native backend requires **macOS 11.3+** and includes a universal binary for Apple Silicon and Intel. Windows uses XInput. There is no need to install Controlify, Controllable, YACL, or separate DLLs.

Controller detection and all buttons, sticks, and triggers were verified with an Xbox Wireless Controller over Bluetooth on an Apple Silicon Mac, in Tekxit with Forge 10.13.4.1558. The version also passed 101 simulated input assertions and 19 cursor capture assertions. Windows and Intel Mac still require hardware testing.

This is an **experimental fork** based on the 1.7.10 branch of the [original project](https://github.com/ljsimin/MinecraftJoypadSplitscreenMod), by Ljubomir Simin, Andrew Hickey, and contributors. Releases in this fork are not official upstream releases.
