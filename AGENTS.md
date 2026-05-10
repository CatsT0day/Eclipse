# EclipseAPI - Architecture Documentation for Claude AI

## Purpose

This document describes the EclipseAPI codebase architecture to help Claude AI understand the project structure, key components, and patterns when working with this codebase.

## Project Overview

EclipseAPI is a Minecraft plugin built for Paper/Spigot servers. It provides 40+ commands, an API for other plugins, and customizable features like homes, warps, economy integration, chat management, and GUI systems.

**Key Technologies:**
- Paper API (1.17.1-R0.1-SNAPSHOT)
- Java 17 (targeting Java 16)
- Gradle with Kotlin DSL
- Shadow plugin for fat JARs
- Adventure Text MiniMessage for formatting
- LuckPerms API for permissions

## Core Architecture Components

### 1. Plugin Instance Agent

**Class:** `Eclipse`  
**Location:** `me.catst0day.Eclipse.Eclipse`

The main plugin instance serves as the central coordinator for all agents in the system. It manages:
- Command registration and execution
- Event listener registration
- Manager initialization and lifecycle
- Translation/loading system
- Global state management (god mode, fly mode, TPA requests, boss bars)

**Key Responsibilities:**
- Singleton pattern for global access via `Eclipse.getI()`
- Dynamic command registration from packages
- Manager lazy initialization
- Message/translation system
- Teleport system with delay support

---

### 2. Manager Agents

Manager agents handle specific domains of functionality within the plugin. Each manager is responsible for a particular aspect of server or player management.

#### EclipseHomeManager
**Location:** `me.catst0day.Eclipse.Managers.EclipseHomeManager`  
**Purpose:** Manages player home locations  
**Features:** Set, delete, and teleport to player homes

#### EclipseWarpManager
**Location:** `me.catst0day.Eclipse.Managers.EclipseWarpManager`  
**Purpose:** Manages server-wide warp points  
**Features:** Set, delete, and teleport to warps

#### EclipsePermissionManager
**Location:** `me.catst0day.Eclipse.Managers.EclipsePermissionManager`  
**Purpose:** Handles permission checking and management  
**Features:** Integration with LuckPerms, custom permission system

#### EclipseAliasManager
**Location:** `me.catst0day.Eclipse.Managers.EclipseAliasManager`  
**Purpose:** Manages command aliases  
**Features:** Custom command aliasing system

#### EclipseEconomyManager
**Location:** `me.catst0day.Eclipse.Managers.EclipseEconomyManager`  
**Purpose:** Economy integration and management  
**Features:** Balance checking, transactions, economy hooks

#### EclipseChatManager
**Location:** `me.catst0day.Eclipse.Managers.EclipseChatManager`  
**Purpose:** Chat formatting and management  
**Features:** Chat colors, formats, mute system

#### EclipseModuleManager
**Location:** `me.catst0day.Eclipse.Managers.EclipseModuleManager`  
**Purpose:** Plugin module management  
**Features:** Enable/disable plugin features dynamically

#### EclipseSQLiteManager
**Location:** `me.catst0day.Eclipse.Managers.Database.EclipseSQLiteManager`  
**Purpose:** Database operations  
**Features:** SQLite database for data persistence

---

### 3. Command Agents

Command agents extend `CommandTemplate` and serve as the primary interface for player and console interactions.

**Base Class:** `CommandTemplate`  
**Location:** `me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate`

**Features:**
- Permission checking
- Cooldown system
- Tab completion
- Player-only command support
- Event firing for command execution
- Dynamic command registration

**Available Commands:**
- `AfkCheck` - Check AFK status
- `Balance` - Economy balance
- `Chat` - Chat management
- `ClearInventory` - Clear player inventory
- `Day/Night` - Time control
- `Delhome/Sethome` - Home management
- `EFly` - Extended fly
- `EcSee` - Ender chest viewing
- `Enchant` - Enchanting
- `Feed/Heal` - Player health/food
- `Fix` - Item repair
- `FlySpeed` - Fly speed control
- `GiveTnt` - Give TNT
- `Gm` - Gamemode switching
- `God` - God mode
- `Home` - Teleport to home
- `InvSee` - Inventory viewing
- `Near` - Find nearby players
- `PTime/PWeather` - Player time/weather
- `Reload` - Reload plugin
- `SetSpawn/Spawn` - Spawn management
- `SetWarp/Warp` - Warp management
- `Spec` - Spectator mode
- `Sudo` - Execute as player
- `Suicide` - Kill player
- `Tp/Tpa/Tpaaccept/Tpdeny/Tphere` - Teleportation
- `Vanish` - Vanish mode
- `afk/back` - AFK system
- `help` - Command help

---

### 4. Entity Agents

Entity agents represent and manage game entities with enhanced functionality.

#### EclipseEntity
**Location:** `me.catst0day.Eclipse.Entity.EclipseEntity`  
**Purpose:** Wrapper for Bukkit entities with enhanced features  
**Features:**
- Entity name translation
- Custom name handling
- Inventory access
- Entity type serialization/deserialization
- Health attribute access
- Subtype detection

#### EclipseEntityType
**Location:** `me.catst0day.Eclipse.Entity.EclipseEntityType`  
**Purpose:** Enum for entity types with version compatibility  
**Features:**
- Cross-version entity type mapping
- Secondary name support
- Type conversion utilities

#### EclipseEntitySubType
**Location:** `me.catst0day.Eclipse.Entity.EclipseEntitySubType`  
**Purpose:** Entity subtype detection (colors, variants, professions)  
**Features:**
- Tame/untamed detection
- Baby/adult detection
- Color variants (sheep, horses, cats, etc.)
- Profession detection (villagers)
- Size detection (slimes, magma cubes)
- Variant detection (rabbits, foxes, pandas, etc.)

---

### 5. Player Agents

Player agents provide enhanced player management and interaction capabilities.

#### EclipsePlr
**Location:** `me.catst0day.Eclipse.Entity.Player.EclipsePlr`  
**Purpose:** Enhanced player wrapper with offline support  
**Features:**
- Offline player support
- Location tracking (logout, death, last teleport)
- Ignore system
- Metadata storage
- Vote tracking
- Async teleportation
- Raw message sending
- Home management integration

#### EclipsePlayerInventory
**Location:** `me.catst0day.Eclipse.Entity.Player.EclipsePlayerInventory`  
**Purpose:** Enhanced inventory management

---

### 6. GUI Agents

GUI agents provide inventory-based user interfaces.

#### Gui
**Location:** `me.catst0day.Eclipse.Entity.Player.Gui`  
**Purpose:** Inventory-based GUI system

#### GuiButton
**Location:** `me.catst0day.Eclipse.Entity.Player.GuiButton`  
**Purpose:** Interactive GUI buttons with click handlers

#### GuiManager
**Location:** `me.catst0day.Eclipse.Entity.Player.GuiManager`  
**Purpose:** GUI lifecycle management

#### GuiListener
**Location:** `me.catst0day.Eclipse.Entity.Player.GuiListener`  
**Purpose:** GUI event handling

---

### 7. Event Listener Agents

Event listeners act as reactive agents that respond to specific game events.

#### EclipseHideAchievements
**Location:** `me.catst0day.Eclipse.EventListeners.EclipseHideAchievements`  
**Purpose:** Hides achievement notifications

#### EclipseOnPlayerJoinEvent
**Location:** `me.catst0day.Eclipse.EventListeners.EclipseOnPlayerJoinEvent`  
**Purpose:** Handles player join events

#### EclipseOnEntityDamageByEntityEvent
**Location:** `me.catst0day.Eclipse.EventListeners.EclipseOnEntityDamageByEntityEvent`  
**Purpose:** Handles entity-vs-entity damage

#### EclipseOnPlayerRespawnEvent
**Location:** `me.catst0day.Eclipse.EventListeners.EclipseOnPlayerRespawnEvent`  
**Purpose:** Handles player respawn events

#### EclipseOnEntityDamageEvent
**Location:** `me.catst0day.Eclipse.EventListeners.EclipseOnEntityDamageEvent`  
**Purpose:** Handles general entity damage

#### EclipseOnItemPickupEvent
**Location:** `me.catst0day.Eclipse.EventListeners.EclipseOnItemPickupEvent`  
**Purpose:** Handles item pickup events

#### EclipseChatListener
**Location:** `me.catst0day.Eclipse.EventListeners.EclipseChatListener`  
**Purpose:** Handles chat events with formatting and filtering

#### EclipseOnCommandEvent
**Location:** `me.catst0day.Eclipse.EventListeners.EclipseOnCommandEvent`  
**Purpose:** Fired before command execution for interception

---

### 8. Bossbar Agents

Bossbar agents provide visual progress indicators.

#### EclipseBossBar
**Location:** `me.catst0day.Eclipse.Bossbar.EclipseBossBar`  
**Purpose:** Custom boss bar implementation  
**Features:**
- Color and style customization
- Time-based progress
- Hide event support

#### EclipseBarColor
**Location:** `me.catst0day.Eclipse.Bossbar.EclipseBarColor`  
**Purpose:** Boss bar color enum

#### EclipseBarStyle
**Location:** `me.catst0day.Eclipse.Bossbar.EclipseBarStyle`  
**Purpose:** Boss bar style enum

---

### 9. Scheduler Agents

Scheduler agents manage asynchronous and timed tasks.

#### EclipseScheduler
**Location:** `me.catst0day.Eclipse.Utils.Schedulers.EclipseScheduler`  
**Purpose:** Custom scheduler wrapper  
**Features:**
- Task scheduling
- Timer tasks
- Async task support

#### EclipseTask
**Location:** `me.catst0day.Eclipse.Utils.Schedulers.EclipseTask`  
**Purpose:** Task representation

#### BukkitTask
**Location:** `me.catst0day.Eclipse.Utils.Schedulers.BukkitTask`  
**Purpose:** Bukkit task wrapper

---

### 10. Particle Agents

Particle agents handle visual particle effects.

#### ParticleManager
**Location:** `me.catst0day.Eclipse.Particles.ParticleManager`  
**Purpose:** Particle effect management

#### ParticleAnim
**Location:** `me.catst0day.Eclipse.Particles.ParticleAnim`  
**Purpose:** Particle animation base

#### SphereAnim
**Location:** `me.catst0day.Eclipse.Particles.SphereAnim`  
**Purpose:** Sphere particle animation

---

### 11. Utility Agents

Utility agents provide helper functions and common operations.

#### ConfigUtil
**Location:** `me.catst0day.Eclipse.Utils.ConfigUtil`  
**Purpose:** Configuration file management

#### EclipseArray
**Location:** `me.catst0day.Eclipse.Utils.EclipseArray`  
**Purpose:** Array utilities

#### ResourceDownloader
**Location:** `me.catst0day.Eclipse.Utils.ResourceDownloader`  
**Purpose:** Resource file downloading

#### TextUtil
**Location:** `me.catst0day.Eclipse.Utils.Text.TextUtil`  
**Purpose:** Text formatting and color codes  
**Features:**
- Hex color translation
- Alternate color codes
- MiniMessage support

#### RawJsonMessage
**Location:** `me.catst0day.Eclipse.Utils.Text.RawJsonMessage`  
**Purpose:** JSON message construction for chat

#### Util
**Location:** `me.catst0day.Eclipse.Utils.Util`  
**Purpose:** General utility functions  
**Features:**
- Logging
- Startup banner
- Load timing

#### VersionChecker
**Location:** `me.catst0day.Eclipse.Utils.VersionChecker`  
**Purpose:** Plugin version checking and updates

---

## Communication Patterns for Claude AI

### 1. Manager Access
All managers are accessed through the main plugin instance:
```java
Eclipse plugin = Eclipse.getI();
EclipseHomeManager homeManager = plugin.getHomeManager();
```

### 2. Player Agent Creation
Player agents are created via the plugin:
```java
EclipsePlr player = plugin.getPlayer(playerUUID);
```

### 3. Event-Driven Communication
Event listeners communicate with other agents through Bukkit's event system:
```java
EclipseOnCommandEvent event = new EclipseOnCommandEvent(sender, commandName, args);
plugin.getServer().getPluginManager().callEvent(event);
```

### 4. Command Registration
Commands are automatically registered from packages using reflection:
```java
registerAllCommandsFromPackage("me.catst0day.Eclipse.Commands.list");
```

---

## Agent Lifecycle

### Initialization Order
1. Plugin instance created
2. Configuration loaded
3. Translations loaded
4. Managers initialized (lazy initialization)
5. Commands registered
6. Event listeners registered
7. Version check performed

### Shutdown Order
1. All Bukkit tasks cancelled
2. Boss bars removed
3. Economy manager shutdown
4. Plugin disabled

---

## Extending the Agent System

### Adding a New Manager
1. Create manager class extending appropriate base
2. Add lazy initialization getter in `Eclipse.java`
3. Initialize in `onEnable()` if needed

### Adding a New Command
1. Extend `CommandTemplate`
2. Implement abstract methods: `perform(CommandSender, Player, String[])`, `perform(Player, String[])`, `tabCompl(Player, String[])`
3. Place in `Commands/list` package for auto-registration

### Adding a New Event Listener
1. Implement appropriate Bukkit event listener
2. Register in `registerEvents()` method
3. Add conditional registration if needed

---

## Dependencies

- **Paper API** (1.17.1-R0.1-SNAPSHOT) - Core Minecraft server API
- **Adventure Text MiniMessage** (4.14.0) - Text formatting
- **Reflections** (0.10.2) - Reflection utilities for command registration
- **LuckPerms API** (5.4) - Permission system integration

---

## Build System

- **Gradle** with Kotlin DSL
- **Shadow Plugin** for creating fat JARs
- **Java 17** toolchain, targeting Java 16
- **Maven Publishing** for JitPack distribution

---

## Version Information

- **Group:** com.github.CatsT0day
- **Artifact:** EclipseAPI
- **Version:** 1.0.0.8
- **Distribution:** JitPack

---

## Important Patterns for Claude AI

### Singleton Pattern
The main plugin instance uses a singleton pattern accessed via `Eclipse.getI()`. Always use this to access the plugin instance rather than creating new instances.

### Lazy Initialization
Managers are lazily initialized through getter methods in the main Eclipse class. This means they're only created when first accessed.

### Dynamic Command Registration
Commands are automatically registered from the `Commands.list` package using reflection. To add a new command:
1. Create a class extending `CommandTemplate` in `Commands/list`
2. Implement the required abstract methods
3. It will be auto-registered on plugin startup

### Event-Driven Architecture
The plugin uses Bukkit's event system extensively. Event listeners are registered in the `registerEvents()` method of the main plugin class.

### Translation System
All user-facing messages should use the translation system via `plugin.getMessage(key)` rather than hardcoded strings. This supports multiple languages.

### Player Wrappers
Always use `EclipsePlr` instead of raw `Player` objects when working with players. This provides offline support and additional functionality.

### Async Operations
Use `EclipseScheduler` for async operations rather than Bukkit's scheduler directly. This provides better error handling and consistency.

---

## Notes

- The plugin uses a singleton pattern for the main instance
- All managers use lazy initialization for performance
- Commands are registered dynamically using reflection
- The plugin supports multiple languages through the translation system
- Async operations are used where possible for performance
- The entity system provides cross-version compatibility
