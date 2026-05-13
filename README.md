# EclipseAPI - 40+ Commands, API, GUI System, Home & Warp System, Economy Integration, Chat Management and a lot more!

## Some features

- **40+ Commands** - Essential server commands including teleportation, player management, economy, and more
- **Modular System** - Enable/disable individual features via configuration
- **Multi-language Support** - Easy translation system with included EN, DE, RU, TR languages
- **API for Developers** - Integrate EclipseAPI features into your own plugins
- **GUI System** - Create inventory-based menus with click handlers
- **Customizable Messages** - All messages are configurable and translatable
- **Home & Warp System** - Player homes and server warps
- **Economy Integration** - Vault-compatible economy support
- **Chat Management** - Chat formatting, colors, and mute system
- **Player Management** - God mode, fly mode, vanish, and more

## Commands

### Tp's
- `/tp <player>` - Teleport to a player
- `/tphere <player>` - Teleport player to you
- `/tpa <player>` - Request teleport to player
- `/tpaaccept` - Accept teleport request
- `/tpdeny` - Deny teleport request
- `/home [name]` - Teleport to home
- `/sethome [name]` - Set a home
- `/delhome [name]` - Delete a home
- `/warp <name>` - Teleport to warp
- `/setwarp <name>` - Set a warp
- `/spawn` - Teleport to spawn
- `/setspawn` - Set spawn location
- `/back` - Return to previous location

### Plr Management
- `/feed` - Feed yourself or others
- `/heal` - Heal yourself or others
- `/gm <mode>` - Change gamemode
- `/fly` - Toggle fly mode
- `/god` - Toggle god mode
- `/vanish` - Toggle vanish mode
- `/spec` - Enter spectator mode
- `/suicide` - Kill yourself
- `/clearinventory` - Clear inventory
- `/invsee <player>` - View player inventory
- `/ecsee <player>` - View ender chest
- `/near` - Find nearby players
- `/sudo <player> <command>` - Execute command as player

### Economy & Items
- `/balance` - Check balance
- `/fix` - Repair held item or all items
- `/enchant <enchantment> [level]` - Enchant held item
- `/givetnt` - Give TNT

### Time & Weather
- `/day` - Set time to day
- `/night` - Set time to night
- `/ptime <time>` - Set personal time
- `/pweather <weather>` - Set personal weather

### Chat & AFK
- `/afk` - Toggle AFK status
- `/chat <message>` - Send chat message
- `/afkcheck <player>` - Check AFK status

### Admin
- `/reload` - Reload plugin configuration
- `/help` - Show command help

## Configuration

### config.yml
```yaml
lang: EN                    # Language file to use (EN, DE, RU, TR)
monochromeMode: false       # Disable colored messages
spawn:                      # Spawn location settings
  world: spawn
  x: 0
  y: 0
  z: 0
  yaw: 0
  pitch: 0
  join:                     # Join title settings
    title: "&6&k|||&r &6Server &k|||"
    subtitle: "&fHave a nice game!"
    fadeIn: 25
    stay: 50
    fadeOut: 25
```

### modules.yml
Enable/disable individual features:
```yaml
modules:
  chat: true
  economy: true
  teleportation: true
  playerManagement: true
  afk: true
  weatherTime: true
  itemSystem: true
  adminCommands: true
```

## Building

```bash
# Build the plugin
./gradlew build

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build
```

## Dependencies

- **Paper** (1.17.1-R0.1-SNAPSHOT) - Core Minecraft server API
- **Adventure MiniMsgs** (4.14.0) - Text formatting
- **Reflections** (0.10.2) - Reflection utilities
- **LuckPerms api** (5.4) - Perms (compile-only)

## Translations

The plugin supports multiple languages. Translation files are located in `src/main/resources/Translations/`:

- `EN.yml` - English (default)
- `DE.yml` - German
- `RU.yml` - Russian
- `TR.yml` - Turkish

To add a new language, copy an existing translation file and translate the messages, then set `lang` in `config.yml` to your language code.

## Permissions

All commands use the permission system. Permissions follow the pattern:
- `eclipse.command.<commandname>` - Base command permission
- `eclipse.command.<commandname>.others` - Execute on other players
- `eclipse.teleport.delay.<seconds>` - Override teleport delay

Example:
```yaml
permissions:
  eclipse.command.god: true
  eclipse.command.god.others: true
  eclipse.teleport.delay.0: true  # Instant teleport
```

## Requirements

- Java 17
- Paper/Spigot 1.17.1 or higher
- Vault (optional, for economy features)
- LuckPerms (optional, for advanced permissions)
## Add EclipseAPI as a dependency

### Gradle (Kotlin DSL)

Add to `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add to dependencies:
```kotlin
dependencies {
    implementation("com.github.CatsT0day:EclipseAPI:Tag")
}
```

### Gradle (Groovy)

Add to `settings.gradle`:
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add to dependencies:
```groovy
dependencies {
    implementation 'com.github.CatsT0day:EclipseAPI:Tag'
}
```

### Maven

Add to `pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.CatsT0day</groupId>
        <artifactId>EclipseAPI</artifactId>
        <version>Tag</version>
    </dependency>
</dependencies>
```

Replace `Tag` with the actual version (e.g., `1.0.0.8`).
## API Examples

### Get a player

```java
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.command.CommandSender;
import java.util.UUID;

public class Example {
    public void runExample(CommandSender sender) {
        UUID playerUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        EclipsePlr player = Eclipse.getI().getPlayer(playerUUID);
        if (player != null) {
            sender.sendMessage("Found player: " + player.getName());
        } else {
            sender.sendMessage("Player not found");
        }
    }
}
```

### Set a home

```java
import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Example {
    public boolean setPlayerHome(Player player, String homeName) {
        EclipsePlr plr = Eclipse.getI().getPlayer(player);
        Location playerLocation = player.getLocation();

        boolean success = plr.setHome(homeName, playerLocation);
        if (success) {
            player.sendMessage("Home '" + homeName + "' set!");
            return true;
        } else {
            player.sendMessage("Failed to set home");
            return false;
        }
    }
}
```

### Create a GUI

```java
import me.catst0day.Eclipse.Entity.Player.Gui;
import me.catst0day.Eclipse.Entity.Player.GuiButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class Example {
    private void showHomesGui(Player player) {
        Gui gui = new Gui(player, "§7Homes", 6);

        int slot = 10;
        for (String home : getHomes(player)) {
            GuiButton homeButton = new GuiButton(Material.PLAYER_HEAD)
                .setName("§a" + home)
                .onLeftClick(p -> {
                    Location homeLocation = Eclipse.getI().getHomeManager()
                        .getHome(p.getUniqueId(), home);
                    if (homeLocation != null) {
                        p.teleport(homeLocation);
                        p.closeInventory();
                    }
                });
            gui.addButton(slot, homeButton);
            slot++;
            if (slot % 9 == 8) slot += 2;
        }

        gui.open();
    }

    private List<String> getHomes(Player player) {
        // Get homes from your data source
        return List.of("home1", "home2");
    }
}
```

### Create a Hologram

```java
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.List;

public class Example {
    public void createHologram(Player player, Location location) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        
        List<String> lines = List.of(
            "&6&lWelcome to the Server!",
            "&fEnjoy your stay",
            "&eOnline players: %online%"
        );
        
        boolean success = manager.createHologram("welcome", location, lines);
        if (success) {
            player.sendMessage("HOooooooooooooooooooooloooooo!");
        } else {
            player.sendMessage("Bro, we cant create holos with the same name");
        }
    }
}
```

### Get and Modify a Hologram

```java
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;

public class Example {
    public void modifyHologram(Player player) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        EclipseHologram hologram = manager.getHologram("welcome");
        
        if (hologram != null) {
            // Add a new line
            hologram.addLine("&aNew line added!");
            
            // Set view distance
            hologram.setViewDistance(64);
            
            // Make it clickable
            hologram.setClickable(true);
            hologram.setClickCommand("warp spawn");
            
            // Set permission requirement
            hologram.setPermission("eclipse.hologram.see");
            
            // Update the hologram for all players
            manager.updateHologram(hologram);
            
            player.sendMessage("Hologram modified!");
        } else {
            player.sendMessage("Hologram not found!");
        }
    }
}
```

### Delete a Hologram

```java
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;

public class Example {
    public void deleteHologram(Player player, String name) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        
        boolean success = manager.deleteHologram(name);
        if (success) {
            player.sendMessage("Hologram '" + name + "' deleted!");
        } else {
            player.sendMessage("Hologram not found!");
        }
    }
}
```

### List All Holograms

```java
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;

public class Example {
    public void listHolograms(Player player) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        
        player.sendMessage("=== mabye, i should put something there ===");
        for (EclipseHologram hologram : manager.getAllHolograms()) {
            player.sendMessage("- " + hologram.getName() + 
                " at " + hologram.getLocation().getWorld().getName() +
                " (" + hologram.getLocation().getBlockX() + ", " +
                hologram.getLocation().getBlockY() + ", " +
                hologram.getLocation().getBlockZ() + ")");
        }
    }
}
```

### "Advanced" Hologram Config

```java
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Holograms.EclipseHologram.FollowType;
import me.catst0day.Eclipse.Holograms.EclipseHologram.TextAlignment;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import java.util.List;

public class Example {
    public void createAdvancedHologram(Player player, Location location) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        
        List<String> lines = List.of(
            "&6&lHOLO",
            "&fWith custom settings",
            "!nextpage!",
            "&ePage 2 content"
        );
        
        boolean success = manager.createHologram("advanced", location, lines);
        
        if (success) {
            EclipseHologram hologram = manager.getHologram("advanced");
            
            // Text settings
            hologram.setTextAlignment(TextAlignment.CENTER);
            hologram.setTextShadow(true);
            hologram.setTextAlpha(255);
            hologram.setBackgroundColor("#FF0000");
            hologram.setBackgroundAlpha(128);
            
            // Animation settings
            hologram.setFadeInTicks(20);
            hologram.setFadeOutTicks(20);
            
            // Display settings
            hologram.setScale(1.5);
            hologram.setFollowType(FollowType.CENTER);
            hologram.setYawOffset(0);
            hologram.setPitchOffset(0);
            
            // Board settings (background board)
            hologram.setBoardEnabled(true);
            hologram.setBoardMaterial(Material.OAK_PLANKS);
            hologram.setBoardScale(1.0);
            hologram.setBoardThickness(0.1);
            
            // Icon settings
            hologram.setIconScale(1.0);
            hologram.setIconYawOffset(0);
            hologram.setIconPitchOffset(0);
            
            // Update interval
            hologram.setUpdateInterval(10);
            
            // Line of sight check
            hologram.setLineOfSight(true);
            
            // Light level
            hologram.setLightLevel(15);
            
            manager.updateHologram(hologram);
            player.sendMessage("idk what to put here");
        }
    }
}
```

### Hologram Pagination

```java
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;

public class Example {
    public void handlePagination(Player player) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        EclipseHologram hologram = manager.getHologram("advanced");
        
        if (hologram != null) {
            // Get current page for player
            int currentPage = hologram.getPlayerPage(player);
            
            // Go to next page
            hologram.nextPage(player);
            
            // Go to previous page
            hologram.prevPage(player);
            
            // Set specific page
            hologram.setPlayerPage(player, 2);
            
            // Get total page count
            int totalPages = hologram.getPageCount();
            
            // Get lines for specific page
            List<String> pageLines = hologram.getLinesForPage(0);
            
            player.sendMessage("Current page: " + currentPage + "/" + totalPages);
        }
    }
}
```

### Show/Hide Holograms for Players

```java
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;

public class Example {
    public void showAllHolograms(Player player) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        manager.showAllHologramsToPlayer(player);
        player.sendMessage("All holograms shown!");
    }
    
    public void hideAllHolograms(Player player) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        manager.hideAllHologramsFromPlayer(player);
        player.sendMessage("All holograms hidden!");
    }
}
```

### Check Hologram Visibility

```java
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHologramManager;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.entity.Player;

public class Example {
    public void checkVisibility(Player player) {
        EclipseHologramManager manager = Eclipse.getI().getHologramManager();
        EclipseHologram hologram = manager.getHologram("welcome");
        
        if (hologram != null) {
            boolean isVisible = hologram.isVisibleTo(player);
            boolean shouldUpdate = hologram.shouldUpdateFor(player);
            
            player.sendMessage("Visible: " + isVisible);
            player.sendMessage("Should update: " + shouldUpdate);
            player.sendMessage("View distance: " + hologram.getViewDistance());
            player.sendMessage("Always visible: " + hologram.isAlwaysVisible());
        }
    }
}
```

---See [LICENSE](LICENSE) and [CONTRIBUTING.md](CONTRIBUTING.md) for more info.


