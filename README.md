# Mob Mounting Control

A server-side Fabric mod for Minecraft 1.20.1 that controls which entities can mount vehicles (boats, minecarts, etc.) using configurable blacklists and whitelists.

## Features

- **Entity Blacklist/Whitelist Mode**: Control which entities can mount vehicles using either blacklist or whitelist approach
- **Vehicle Blacklist/Whitelist Mode**: Control which vehicle types are affected by the mod using either blacklist or whitelist approach  
- **Dual-Layer Control**: First filter vehicles by type, then check entity permissions for those vehicles
- **Player Exception**: Option to always allow players to mount vehicles regardless of other settings
- **Logging**: Optional logging of mounting attempts for debugging
- **Runtime Configuration**: JSON-based configuration file that can be edited while the server is running

## Installation

1. Download the latest release from the releases page
2. Place the `.jar` file in your server's `mods` folder
3. Start your server - the mod will create a default configuration file
4. Edit the configuration file if needed and restart the server

## Configuration

The mod creates a configuration file at `config/mobmountingcontrol.json` with the following options:

```json
{
  "enableMod": true,
  "defaultAllowMounting": true,
  "playersAlwaysAllowed": true,
  "entityBlacklist": [
    "minecraft:zombie",
    "minecraft:skeleton",
    "minecraft:creeper",
    "minecraft:spider"
  ],
  "entityWhitelist": [
    "minecraft:villager",
    "minecraft:horse",
    "minecraft:donkey",
    "minecraft:mule"
  ],
  "defaultAllowVehicles": true,
  "vehicleBlacklist": [
    "minecraft:tnt_minecart"
  ],
  "vehicleWhitelist": [
    "minecraft:boat",
    "minecraft:chest_boat",
    "minecraft:minecart",
    "minecraft:chest_minecart",
    "minecraft:furnace_minecart",
    "minecraft:hopper_minecart"
  ],
  "logMountingAttempts": false
}
```

### Configuration Options

- **enableMod**: Enable/disable the mod entirely
- **defaultAllowMounting**: 
  - `true` = Entity blacklist mode (allow all entities except blacklisted)
  - `false` = Entity whitelist mode (deny all entities except whitelisted)
- **playersAlwaysAllowed**: If true, players can always mount vehicles regardless of other settings
- **entityBlacklist**: List of entity types that cannot mount vehicles (when in blacklist mode)
- **entityWhitelist**: List of entity types that can mount vehicles (when in whitelist mode)
- **defaultAllowVehicles**: 
  - `true` = Vehicle blacklist mode (control all vehicles except blacklisted)
  - `false` = Vehicle whitelist mode (only control whitelisted vehicles)
- **vehicleBlacklist**: List of vehicle types to exclude from control (when in blacklist mode)
- **vehicleWhitelist**: List of vehicle types to control (when in whitelist mode)
- **logMountingAttempts**: Enable logging of mounting attempts for debugging

### How the Dual-Layer System Works

1. **Vehicle Filter**: First, the mod checks if the vehicle type should be controlled based on `defaultAllowVehicles` and the vehicle lists
2. **Entity Permission**: If the vehicle is controlled, then check if the entity has permission to mount based on `defaultAllowMounting` and the entity lists
3. **Player Override**: Players bypass entity permissions if `playersAlwaysAllowed` is true

## Usage Examples

### Prevent hostile mobs from mounting boats only
```json
{
  "defaultAllowMounting": true,
  "entityBlacklist": [
    "minecraft:zombie",
    "minecraft:skeleton", 
    "minecraft:creeper",
    "minecraft:spider"
  ],
  "defaultAllowVehicles": false,
  "vehicleWhitelist": [
    "minecraft:boat",
    "minecraft:chest_boat"
  ]
}
```

### Only allow specific mobs on any vehicle except TNT minecarts
```json
{
  "defaultAllowMounting": false,
  "entityWhitelist": [
    "minecraft:villager",
    "minecraft:horse",
    "minecraft:cat"
  ],
  "defaultAllowVehicles": true,
  "vehicleBlacklist": [
    "minecraft:tnt_minecart"
  ]
}
```

### Control all vehicles but only block zombies from everything
```json
{
  "defaultAllowMounting": true,
  "entityBlacklist": [
    "minecraft:zombie"
  ],
  "defaultAllowVehicles": true,
  "vehicleBlacklist": []
}
```

## Building from Source

1. Clone this repository
2. Run `./gradlew build`
3. The built mod will be in `build/libs/`

## Requirements

- Minecraft 1.20.1
- Fabric Loader 0.14.21 or higher
- Fabric API

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

If you encounter any issues or have feature requests, please open an issue on the GitHub repository.