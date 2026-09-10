# Joypad Enhanced — Minecraft 1.7.10

Native **macOS Bluetooth/USB controller support**, **XInput fixes for Windows**, and controller UI artwork made with **PixelLab**.

**[Download 0.4.0](https://github.com/patodois/MinecraftJoypadSplitscreenMod/releases/tag/v0.4.0)** · **[Installation, controls and build instructions](MinecraftJoypadSplitscreenMod/README.md)**

## New in 0.4.0

- All **27 controller icons** regenerated or refined in PixelLab.
- PixelLab textures for every mod-owned button and slider, including highlighted and disabled states.
- Minecraft-inspired stone-gray surfaces, grass-green highlights and warm accents.
- Clear controller labels and directional arrows, with layout guides used for precise PixelLab refinements.
- Versioned source artwork and provenance; no API key or online image generation is needed to build or run the mod.

Console defaults and automatic mouse capture remain: A confirms, Y opens inventory or goes back, X splits stacks, B quick-moves items, LS sprints and RS sneaks. Icons follow remapped bindings.

## Installation

1. Use Minecraft **1.7.10 + Forge + Java 8**.
2. Close the game and replace the previous JoypadMod JAR in `mods` with the new one.
3. Open **Options → Controls**, select your device, and open **Test controller**.

The macOS backend requires **macOS 11.3+** and includes Apple Silicon and Intel binaries. No separate PixelLab, controller library, Controlify, Controllable or YACL installation is required.

## Compatibility

Xbox Wireless Controller Bluetooth detection and input were verified on Apple Silicon in Tekxit with Forge **10.13.4.1558**. A successful test was also reported on a **2017 Intel Mac**. Windows hardware still needs testing. The new artwork passes asset and rendering-layout checks and still needs full in-game confirmation.

This is an **experimental fork** of the [original JoypadMod](https://github.com/ljsimin/MinecraftJoypadSplitscreenMod), based on its 1.7.10 branch. Original authors Ljubomir Simin, Andrew Hickey and contributors are credited; releases here are not official upstream releases.
