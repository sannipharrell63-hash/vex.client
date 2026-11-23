# VEX CLIENT — Fabric 1.21.11

A client-side Fabric mod starter for Minecraft Java 1.21.11. It adds a dark purple VEX CLIENT menu opened with **Right Shift**. The menu contains saved preference switches for the planned item-visual customisation categories.

## Current prototype scope

- Fabric client entrypoint and Right Shift keybind.
- Black / neon-purple menu.
- Preference toggles for crystals, respawn anchors, pearls, golden apples, swords, armour HUD, obsidian, glowstone and XP bottles.
- Toggle states are saved to `config/vexclient.properties`.

**Important:** These are currently menu preferences only. The visual/gameplay effects behind those switches are not implemented yet. This prototype does not include aim assist, auto-clicking, velocity changes, or other combat automation.

## Build

Requirements: Java 21 and Gradle 8.14 or newer.

From this folder run:

```bash
gradle build
```

The remapped mod JAR will be in `build/libs/` (the file without `-sources`). Put it in your Minecraft `mods` folder alongside Fabric API for Minecraft 1.21.11.

## Version details

- Minecraft: 1.21.11
- Loader: 0.18.1
- Fabric API: 0.141.6+1.21.11
- Java: 21

## License

MIT
