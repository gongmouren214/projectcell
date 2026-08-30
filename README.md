# ProjectCell

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-blue)](https://minecraft.net)
[![Modloader](https://img.shields.io/badge/Modloader-NeoForge-orange)](https://neoforged.net)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

**ProjectCell** is an AE2 storage cell addon that bridges **ProjectE** with **Applied Energistics 2**.

The EMC Storage Cell exposes the player's ProjectE transmutation knowledge and EMC balance as an AE2 storage cell, allowing you to:

- **Insert** items into the cell to convert them into EMC
- **Extract** items from the cell that you have transmutation knowledge for, paying from your EMC balance

## Requirements

- Minecraft **1.21.1**
- NeoForge **21.1.119+**
- Applied Energistics 2 (**19.2.11+**)
- ProjectE (**1.1.0+**)

## Features

- Stores your ProjectE EMC balance as an AE2 storage cell
- Insert any item with an EMC value to convert it into EMC
- Extract any item you have transmutation knowledge for, using your stored EMC
- NBT-aware filtering to avoid data-loss on items with custom NBT
- Client-side EMC formatting display

## Downloads

See [Releases](../../releases) for the latest builds.

## Building from Source

Requires **JDK 21**.

```sh
./gradlew build
```

The built jar will be located in `build/libs/`.

## License

This project is licensed under the [MIT License](LICENSE).
