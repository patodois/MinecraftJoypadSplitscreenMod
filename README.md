# Joypad Enhanced — Minecraft 1.7.10

Native **macOS Bluetooth/USB controller support**, **XInput fixes for Windows**, and a Minecraft-inspired controller interface.

**[Download 0.3.0](https://github.com/patodois/MinecraftJoypadSplitscreenMod/releases/tag/v0.3.0)** · **[Installation, controls and build instructions](MinecraftJoypadSplitscreenMod/README.md)**

## New in 0.3.0

- Pixel-art button icons in contextual hints, bindings and controller diagnostics.
- Stone-gray buttons, grass-green highlights and warm wood accents.
- A console-style default profile: A confirms, Y opens inventory or goes back, X splits stacks, B quick-moves items, LS sprints and RS sneaks.
- Icons follow remapped bindings. Recognizable legacy defaults migrate once; different custom mappings are preserved.
- Automatic in-game mouse capture from 0.2.1 remains enabled by default.

## Installation

1. Use Minecraft **1.7.10 + Forge + Java 8**.
2. Close the game and replace the previous JoypadMod JAR in `mods` with the new one.
3. Open **Options → Controls**, select your device, and open **Test controller**.

The macOS backend requires **macOS 11.3+** and includes Apple Silicon and Intel binaries. The device appears as `macOS - Xbox Wireless Controller` or its corresponding name. No separate controller library, Controlify, Controllable or YACL installation is required.

## Compatibility

Xbox Wireless Controller Bluetooth detection and input were verified on Apple Silicon in Tekxit with Forge **10.13.4.1558**. A successful test was also reported on a **2017 Intel Mac**. Windows hardware still needs testing. The new 0.3.0 visuals and profile changes pass automated checks and still need full in-game validation.

This is an **experimental fork** of the [original JoypadMod](https://github.com/ljsimin/MinecraftJoypadSplitscreenMod), based on its 1.7.10 branch. Original authors Ljubomir Simin, Andrew Hickey and contributors are credited; releases here are not official upstream releases.
