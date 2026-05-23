# Contributing to Eclipse

Thank you for your interest in contributing to EclipseAPI! I welcome contributions of all kinds — from fixing bugs to implementing new features. Please read these guidelines carefully before submitting your work.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Branching Strategy](#branching-strategy)
- [Code Style & Guidelines](#code-style--guidelines)
- [Adding New Commands](#adding-new-commands)
- [Adding New Features](#adding-new-features)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Pull Request Process](#pull-request-process)

## Getting Started

### 1. Fork the Repository

Click the **Fork** button in the top‑right corner of the [Eclipse repository page](https://github.com/CatsT0day/Eclipse) to create your own copy.

### 2. Clone Your Fork

```bash
git clone https://github.com/CONTRIBUTOR-PUT-YOUR_USERNAME/Eclipse.git
cd Eclipse
```

### 3. Set Up Upstream Remote

Keep your fork in sync with the original repository:

```bash
git remote add upstream https://github.com/CatsT0day/Eclipse.git
```

## Development Setup

### Prerequisites

- **Java 17** or higher
- **Gradle 8.0+** (included via wrapper)
- **IDE**: IntelliJ IDEA (recommended) or Eclipse
- **Git**

### Building the Project

```bash
# Build the plugin
./gradlew build

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build

# Run shadow task to create fat JAR
./gradlew shadowJar
```

The built JAR will be located in `build/libs/`.

### IDE Configuration

#### IntelliJ IDEA

1. Open the project directory
2. Gradle will automatically import the project
3. Ensure the JDK is set to Java 17
4. Enable annotation processing if needed

#### Project Structure in IDE

- `src/main/java/` - Main source code
- `src/main/resources/` - Configuration files and translations
- `build.gradle.kts` - Gradle build configuration
- `settings.gradle.kts` - Gradle settings


## Architecture Overview

EclipseAPI uses an **agent-based architecture** where components are organized as specialized agents that handle specific domains. For detailed architecture documentation, see [AGENTS.md](AGENTS.md).

### Key Components

- **Plugin Instance Agent** (`Eclipse`): Central coordinator for all agents
- **Manager Agents**: Handle specific domains (homes, warps, economy, chat, etc.)
- **Command Agents**: Extend `CommandTemplate` for player/console interactions
- **Entity Agents**: Enhanced wrappers for Bukkit entities and players
- **GUI Agents**: Inventory-based user interface system
- **Event Listener Agents**: Reactive agents responding to game events
- **Bossbar Agents**: Visual progress indicators
- **Scheduler Agents**: Asynchronous and timed task management
- **Particle Agents**: Visual particle effects
- **Utility Agents**: Helper functions and common operations

### Communication Patterns

- All managers are accessed through the main plugin instance: `Eclipse.getI()`
- Player agents are created via: `Eclipse.getI().getPlayer(playerUUID)`
- Event-driven communication through Bukkit's event system
- Commands are automatically registered from packages using reflection

## Branching Strategy

Never work directly on the `main` branch.

### Create a New Branch

For each feature or bug fix, create a dedicated branch:

```bash
# Feature branch
git checkout -b feature/add-new-command

# Bugfix branch
git checkout -b bugfix/fix-null-pointer

# Documentation branch
git checkout -b docs/update-readme
```

### Branch Naming Convention

- `feature/` - New features
- `bugfix/` - Bug fixes
- `refactor/` - Code refactoring
- `docs/` - Documentation updates
- `test/` - Test additions or modifications

### Keep Branches Focused

**One branch = one task.** Avoid mixing unrelated changes in a single branch.

### Sync Your Branch

Before starting new work, update your local `main` branch:

```bash
git fetch upstream
git checkout main
git merge upstream/main
git checkout your-branch
git rebase main
```

## Code Style & Guidelines

### General Principles

- Follow the existing code style of the project
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Keep methods focused and concise
- Avoid code duplication

### Eclipse Wrapper Pattern

**Important**: For any Bukkit features, create Eclipse wrapper classes instead of using Bukkit APIs directly. This makes it easier to update and maintain the code.

**Example:**
```java
// Instead of:
Player player = Bukkit.getPlayer(uuid);
player.sendMessage("Hello, World!");

// Use:
EclipsePlr player = Eclipse.getI().getPlayer(uuid);
player.sendMsg("Hello, World!"); 
// this thing is also translated with MINIMESSAGE and "&" colors
```

### Command Implementation

All commands must extend `CommandTemplate`:

```java
package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CMd extends CommandTemplate {

  public CMd(Eclipse mainclass) {
    super(mainclass, "put_name_here", List.of("put_alias_here"), EclipsePermissionManager.EclipsePerm.PUT_YOUR_PERMISSION_HERE, true, 0, "put explanation here");
    setTabCompleteArguments(List.of("set", "reset", "info", "add", "center"));
  }

  @Override
  protected boolean perform(Player player, String[] args) {
    return execute(player, args);
  }

  @Override
  protected boolean perform(CommandSender sender, Player unused, String[] args) {
     execute(sender, args);
  }

  private boolean execute(CommandSender sender, String[] args) {
    // execution logic (optianal, you can put this logic into first perform)
  }

  @Override
  protected List<String> tabCompl(Player player, String[] args) {
      // tab compl. logic
  }
}

```
```java
or
package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CMd extends CommandTemplate {

public CMd(Eclipse mainclass) {
super(mainclass, "put_name_here", List.of("put_alias_here"), EclipsePermissionManager.EclipsePerm.PUT_YOUR_PERMISSION_HERE, true, 0, "put explanation here");
setTabCompleteArguments(List.of("set", "reset", "info", "add", "center"));
}

@Override
protected boolean perform(Player player, String[] args) {
return execute(player, args);
}

@Override
protected boolean perform(CommandSender sender, Player unused, String[] args) {
// command logic
}

private boolean execute(CommandSender sender, String[] args) {
// execution logic (optianal, you can put this logic into first perform)
}

@Override
protected List<String> tabCompl(Player player, String[] args) {
// tab compl. logic
}
}
```


### Code Formatting

- Use 4 spaces for indentation (no tabs)
- Maximum line length: 120 characters
- Place opening braces on the same line
- Use camelCase for variables and methods
- Use PascalCase for classes

### Where do you use comments

- Add Javadoc for all public classes and methods
- Use inline comments for complex logic
- Keep comments up-to-date with code changes

## Adding New Commands

### Step 1: Create Command Class

Create a new class in `src/main/java/me/catst0day/Eclipse/Commands/list/`:

```java
// look up, its there
```

### Step 2: Add Translation

Add command messages to translation files in `src/main/resources/Translations/`:

```yaml
# EN.yml or DE.yml or RU.yml or TR.yml, you should edit all of them, you can use Google translator: 
  https://translate.google.com/?sl=en&tl=ru&op=translate 
mycommand:
  usage: "/mycommand [args]"
  description: "Description of my command"
  success: "Command executed successfully!"
```

### Step 3: Register Command

Commands are automatically registered from the `Commands.list` package. No manual registration needed.

### Step 4: Add Permission

Add permission to the EclipsePermissionManager (EclipsePerm)

```java
  CMD("Eclipse.idkwhattoputhere", "this does something")
```

## Adding New Features

### Creating a Manager

If your feature requires persistent state or complex logic, create a manager:

```java
package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;

public class EclipseCatMarathonManager {
    private final Eclipse plugin;

    public EclipseCatMarathonManager(Eclipse plugin) {
        this.plugin = plugin;
    }

    // Cat Marathon (jk. your logic here)
}
```

Register the manager in the main `Eclipse` class:

```java
private getCatMarathonManager catMarathonManager;

public catMarathonManager getCatMarathonManager() {
    if (catMarathonManager == null) {
        catMarathonManager = new EclipseMyFeatureManager(this);
    }
    return myFeatureManager;
}
```

### Creating Event Listeners

Create event listeners in `src/main/java/me/catst0day/Eclipse/EventListeners/`:

```java
package me.catst0day.Eclipse.EventListeners;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class EclipseOnCatMarathonEvent implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    // Cat Marathon (jk. your logic here)
  }
}
```

Register the listener in the main plugin class:

```java
getServer().getPluginManager().registerEvents(new EclipseOnMyEvent(), this);
```

## Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests MyTestClass
```

### Test Guidelines

- Write unit tests for new features
- Test edge cases and error conditions
- Ensure tests are fast and independent
- Use descriptive test names

### Manual Testing

1. Build the plugin: `./gradlew build`
2. Copy the JAR from `build/libs/` to your test server's `plugins/` folder
3. Restart the server
4. Test your changes thoroughly

## Submitting Changes

### Commit Messages

Use clear, descriptive commit messages:

```
type(scope): subject

body

footer
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (no, that doesn't mean, that you can replace the whole project stile with your own)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `mt`: Maintenance tasks

**Example:**
```
feat(commands): add /mycommand command

Implement new command for feature X.
- Add MyCommand class
- Add translations
- Add permissions

Closes #123
```

### Before Submitting

- [ ] Code follows the project style guidelines
- [ ] Code is properly documented
- [ ] Tests pass locally
- [ ] Manual testing completed
- [ ] Translation files updated (if needed)
- [ ] Commit messages are clear
- [ ] Branch is up-to-date with upstream/main

## Pull Request Process

### 1. Push Your Changes

```bash
git push origin your-branch-name
```

### 2. Create Pull Request

1. Go to the repository on GitHub
2. Click "New Pull Request"
3. Select your branch
4. Fill in the PR template:
   - **Title**: Clear description of changes
   - **Description**: Detailed explanation of what you did and why
   - **Related Issues**: Link to any related issues
   - **Testing**: Describe how you tested your changes

### 3. PR Review

- Address review feedback promptly
- Keep the PR focused and small
- Respond to questions and comments
- Update the PR as needed

### 4. Merge

Once approved, your PR will be merged, or skipped. After merging:

```bash
# Delete your local branch
git branch -d your-branch-name

# Update your main branch
git checkout main
git pull upstream main
```
if skipped/does not merge/you didn't follow the rules:
```bash
# Delete your local branch
git branch -d your-branch-name
```

## Getting Help

If you need help:

1. Check existing issues and discussions
2. Read the [this file](AGENTS.md) for project architecture details (made by AI for AI, lol)
3. Review similar code in the codebase
4. Ask questions in issues or discussions

## License

By contributing, you agree that your contributions will be licensed under the same license as the project (see [[click]](LICENSE)).

---

Happy coding! We look forward to seeing your contributions.