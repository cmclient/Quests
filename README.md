# Quests Minecraft Plugin

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.18.2-brightgreen)
![Spigot Version](https://img.shields.io/badge/Spigot-1.18.2-brightgreen)
![Java Version](https://img.shields.io/badge/Java-17-brightgreen)
![Dependencies](https://img.shields.io/badge/Dependencies-Citizens%2C%20MMOCore%2C%20MMOItems-brightgreen)

A custom Minecraft plugin that adds quest functionality to your Minecraft server. Players can create, configure, and complete quests, making your server's gameplay more engaging and interactive.

## Warning
- This project is unfinished, some features are broken and there are currently no plans to complete this project.

## Features

- Create and manage quests in Minecraft.
- Customizable quest configurations, allowing server owners and users to define their quests.
- Integration with:
  - [Citizens](https://www.spigotmc.org/resources/citizens.13811/)
  - [MMOCore](https://www.spigotmc.org/resources/mmocore.60824/)
  - [MMOItems](https://www.spigotmc.org/resources/mmoitems.52764/)

## Requirements

Make sure you have the following requirements before installing the plugin:

- Minecraft Version: 1.18.2
- Spigot Version: 1.18.2
- Java Version: 17
- MySQL server: 8
- Dependencies:
  - [Citizens](https://www.spigotmc.org/resources/citizens.13811/)
  - [MMOCore](https://www.spigotmc.org/resources/mmocore.60824/)
  - [MMOItems](https://www.spigotmc.org/resources/mmoitems.52764/)

## Installation

1. Download the latest release from the [Releases](https://github.com/cmclient/Quests/releases) section.
2. Place the downloaded JAR file into your server's `plugins` folder.
3. Start or restart your Minecraft server to copy default config and quests.
4. Configure database connection in config.yml and quests in quests.yml.
5. Restart your Minecraft server.

## Configuration

- You can customize quest settings and create your quests by editing the plugin's configuration files.

### Example Quest Configuration (quests.yml)

```yaml
quests:
  0: # NPC ID from the Citizens plugin
    subquests:
      rybak-1: # Unique subquest ID
        title: '&2&lRYBAK I'
        rewards:
          - 'give {PLAYER} diamond_block 64'
        action: ITEM
        mmocore-required-lvl: 0
        mmoitem-type: AXE
        mmoitem-id: 'BONE_CRACKER'
        mob-name: ''
        amount: 10
```

In this example, you have a quest with the ID 0 associated with an NPC from the Citizens plugin. It provides a reward of 64 diamond blocks to the player upon completion by selling 10 "BONE_CRACKER" axes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

If you have any questions, suggestions, or encounter issues, feel free to reach out to the project maintainers:

- cmclient <support@cmclient.pl>

Enjoy using the Quests Minecraft Plugin!
