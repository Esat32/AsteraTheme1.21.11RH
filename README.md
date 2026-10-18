# Astera Theme — RusherHack 1.21.11

<p align="center">
  <img src="https://github.com/user-attachments/assets/3a24148f-2451-4d53-a786-04787cab0290" alt="Astera ClickGUI" width="800"/>
</p>

A clean, Astera client style **ClickGUI theme** for RusherHack. Periwinkle category panels, soft tinted bodies and highlighted rows for enabled modules. It looks just like Astera, but runs on RusherHack.

## Features

- **Astera look**: solid header bar, 1px outline, see-through panel body and filled rows for enabled modules.
- **Adjustable scale**: make the whole GUI bigger or smaller with the `Scale` setting (0.5 – 2).
- **Full color control**: theme color, body/enabled/hover opacity, and text, enabled text, header and value colors.
- **Every setting type**: toggles, sliders, enums/modes, binds, text fields and colors (RGBA, rainbow, sync).
- **Astera category names**: Miscellaneous → Misc, Render → Visual, Client → Core (can be turned off).
- **Draggable panels**: drag a header to move a panel, scroll to move all panels.

## Installation

1. Add the JVM flag `-Drusherhack.enablePlugins=true` to your Minecraft JVM arguments.
2. Download `astera-theme-1.0.0.jar` from the [Releases](https://github.com/Esat32/AsteraTheme1.21.11RH/releases) page.
3. Put it in `.minecraft/rusherhack/plugins/`.
4. Launch the game and check that it loaded with `*plugins`.
5. Open the ClickGUI, go to **Core → Theme** and pick **Astera**.

## Usage

| Action | Result |
|---|---|
| Left click a module | Toggle it |
| Right click a module | Show / hide its settings |
| Drag a panel header | Move the panel |
| Right click a panel header | Collapse the panel |
| Mouse wheel | Scroll all panels |
| Left click a bind, then press a key | Set the bind (Esc / Delete / Backspace clears it) |

## Settings

| Setting | Description |
|---|---|
| `Color` | Main theme color (header, outline, highlights) |
| `Scale` | Size of the whole ClickGUI, applied when you release the slider |
| `TextShadow` | Text shadow, off by default like Astera |
| `BodyAlpha` | Opacity of the panel body tint |
| `EnabledAlpha` | Opacity of enabled module rows |
| `HoverAlpha` | Opacity of the hover overlay |
| `TextColor` / `EnabledTextColor` / `HeaderTextColor` / `ValueColor` | Text colors |
| `AsteraNames` | Use Astera category names |
| `ScrollSpeed` | How far the mouse wheel moves the panels |

> Tip: for the closest Astera look, turn on **CustomFont** and pick a thin font.

## Building

Requires JDK 21.

```sh
./gradlew build
```

The jar is written to `build/libs/astera-theme-1.0.0.jar`.
