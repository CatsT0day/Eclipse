package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Holograms.EclipseHologram;
import me.catst0day.Eclipse.Holograms.EclipseHoloCFGGui;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class Hologram extends CommandTemplate {

    private static final Pattern HOLOGRAM_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,32}$");
    private static final Pattern SAFE_COMMAND_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-:/@\\s\\[\\]{}.,!?%]+$");
    private static final int MAX_LINE_LENGTH = 256;
    private static final int MAX_COMMAND_LENGTH = 256;
    private static final int MAX_PERMISSION_LENGTH = 128;

    public Hologram(Eclipse plugin) {
        super(plugin, "hologram", List.of("holo"), EclipsePermissionManager.EclipsePerm.HOLOGRAMS, true, 0, "Manage holograms");
        tabCompleteArguments = Arrays.asList("create", "delete", "edit", "list", "move", "info", "near", "reload", "set", "page", "toggle", "addline", "removeline", "setline", "copy");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                handleCreate(player, args);
                break;
            case "delete":
                handleDelete(player, args);
                break;
            case "edit":
                handleEdit(player, args);
                break;
            case "list":
                handleList(player);
                break;
            case "move":
                handleMove(player, args);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "near":
                handleNear(player);
                break;
            case "reload":
                handleReload(player);
                break;
            case "set":
                handleSet(player, args);
                break;
            case "page":
                handlePage(player, args);
                break;
            case "toggle":
                handleToggle(player, args);
                break;
            case "addline":
                handleAddLine(player, args);
                break;
            case "removeline":
                handleRemoveLine(player, args);
                break;
            case "setline":
                handleSetLine(player, args);
                break;
            case "copy":
                handleCopy(player, args);
                break;
            default:
                sendHelp(player);
        }
        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("hologramCreateUsage"));
            return;
        }

        String name = args[1];
        if (!isValidHologramName(name)) {
            player.sendMessage(plugin.getMessage("hologramInvalidName"));
            return;
        }

        if (plugin.getHologramManager().hologramExists(name)) {
            player.sendMessage(plugin.getMessage("hologramAlreadyExists").replace("%name%", name));
            return;
        }

        String line = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        List<String> lines = new ArrayList<>();
        lines.add(sanitizeLine(line));

        Location loc = player.getLocation().add(0, 1.5, 0);
        if (plugin.getHologramManager().createHologram(name, loc, lines)) {
            player.sendMessage(plugin.getMessage("hologramCreated").replace("%name%", name));
        } else {
            player.sendMessage(plugin.getMessage("hologramCreateFailed"));
        }
    }

    private boolean isValidHologramName(String name) {
        return name != null && HOLOGRAM_NAME_PATTERN.matcher(name).matches();
    }

    private String sanitizeLine(String line) {
        if (line == null) return "";
        if (line.length() > MAX_LINE_LENGTH) {
            line = line.substring(0, MAX_LINE_LENGTH);
        }
        return line.replace("\n", " ").replace("\r", " ").replace("\t", " ");
    }

    private String sanitizeCommand(String command) {
        if (command == null) return "";
        if (command.length() > MAX_COMMAND_LENGTH) {
            command = command.substring(0, MAX_COMMAND_LENGTH);
        }
        if (!SAFE_COMMAND_PATTERN.matcher(command).matches()) {
            return "";
        }
        return command;
    }

    private String sanitizePermission(String permission) {
        if (permission == null) return null;
        if (permission.length() > MAX_PERMISSION_LENGTH) {
            permission = permission.substring(0, MAX_PERMISSION_LENGTH);
        }
        if (!permission.matches("^[a-zA-Z0-9_\\.-]+$")) {
            return null;
        }
        return permission;
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("hologramDeleteUsage"));
            return;
        }

        String name = args[1];
        if (plugin.getHologramManager().deleteHologram(name)) {
            player.sendMessage(plugin.getMessage("hologramDeleted").replace("%name%", name));
        } else {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
        }
    }

    private void handleEdit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("hologramEditUsage"));
            return;
        }

        String name = args[1];
        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        new EclipseHoloCFGGui(plugin, hologram, player).open();
    }

    private void handleList(Player player) {
        List<String> names = plugin.getHologramManager().getHologramNames();
        if (names.isEmpty()) {
            player.sendMessage(plugin.getMessage("hologramListEmpty"));
            return;
        }

        player.sendMessage(plugin.getMessage("hologramListHeader").replace("%count%", String.valueOf(names.size())));
        for (String name : names) {
            EclipseHologram holo = plugin.getHologramManager().getHologram(name);
            String location = String.format("%.0f, %.0f, %.0f", 
                    holo.getLocation().getX(), holo.getLocation().getY(), holo.getLocation().getZ());
            player.sendMessage(plugin.getMessage("hologramListItem")
                    .replace("%name%", name)
                    .replace("%location%", location)
                    .replace("%lines%", String.valueOf(holo.getLines().size())));
        }
    }

    private void handleMove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("hologramMoveUsage"));
            return;
        }

        String name = args[1];
        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        hologram.setLocation(player.getLocation().add(0, 1.5, 0));
        plugin.getHologramManager().updateHologram(hologram);
        player.sendMessage(plugin.getMessage("hologramMoved").replace("%name%", name));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("hologramInfoUsage"));
            return;
        }

        String name = args[1];
        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        player.sendMessage(plugin.getMessage("hologramInfoHeader").replace("%name%", name));
        player.sendMessage(plugin.getMessage("hologramInfoWorld").replace("%world%", hologram.getLocation().getWorld().getName()));
        player.sendMessage(plugin.getMessage("hologramInfoLocation").replace("%x%", String.format("%.2f", hologram.getLocation().getX()))
                .replace("%y%", String.format("%.2f", hologram.getLocation().getY()))
                .replace("%z%", String.format("%.2f", hologram.getLocation().getZ())));
        player.sendMessage(plugin.getMessage("hologramInfoLines").replace("%count%", String.valueOf(hologram.getLines().size())));
        player.sendMessage(plugin.getMessage("hologramInfoViewDistance").replace("%distance%", String.valueOf(hologram.getViewDistance())));
        player.sendMessage(plugin.getMessage("hologramInfoAlwaysVisible").replace("%status%", 
                hologram.isAlwaysVisible() ? plugin.getMessage("enabled") : plugin.getMessage("disabled")));
    }

    private void handleNear(Player player) {
        List<EclipseHologram> nearby = plugin.getHologramManager().getAllHolograms().stream()
                .filter(h -> h.getLocation().getWorld().equals(player.getWorld()))
                .filter(h -> h.getLocation().distance(player.getLocation()) <= 50)
                .toList();

        if (nearby.isEmpty()) {
            player.sendMessage(plugin.getMessage("hologramNearEmpty"));
            return;
        }

        player.sendMessage(plugin.getMessage("hologramNearHeader").replace("%count%", String.valueOf(nearby.size())));
        for (EclipseHologram holo : nearby) {
            double distance = holo.getLocation().distance(player.getLocation());
            player.sendMessage(plugin.getMessage("hologramNearItem")
                    .replace("%name%", holo.getName())
                    .replace("%distance%", String.format("%.1f", distance)));
        }
    }

    private void handleReload(Player player) {
        player.sendMessage(plugin.getMessage("hologramReloaded"));
    }

    private void sendHelp(Player player) {
        player.sendMessage(plugin.getMessage("hologramHelpHeader"));
        player.sendMessage(plugin.getMessage("hologramHelpCreate"));
        player.sendMessage(plugin.getMessage("hologramHelpDelete"));
        player.sendMessage(plugin.getMessage("hologramHelpEdit"));
        player.sendMessage(plugin.getMessage("hologramHelpList"));
        player.sendMessage(plugin.getMessage("hologramHelpMove"));
        player.sendMessage(plugin.getMessage("hologramHelpInfo"));
        player.sendMessage(plugin.getMessage("hologramHelpNear"));
        player.sendMessage(plugin.getMessage("hologramHelpSet"));
        player.sendMessage(plugin.getMessage("hologramHelpPage"));
        player.sendMessage(plugin.getMessage("hologramHelpToggle"));
        player.sendMessage(plugin.getMessage("hologramHelpAddLine"));
        player.sendMessage(plugin.getMessage("hologramHelpRemoveLine"));
        player.sendMessage(plugin.getMessage("hologramHelpSetLine"));
        player.sendMessage(plugin.getMessage("hologramHelpCopy"));
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(plugin.getMessage("hologramSetUsage"));
            return;
        }

        String name = args[1];
        if (!isValidHologramName(name)) {
            player.sendMessage(plugin.getMessage("hologramInvalidName"));
            return;
        }

        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        String setting = args[2].toLowerCase();
        String value = args[3];

        switch (setting) {
            case "clickable":
                hologram.setClickable(Boolean.parseBoolean(value));
                break;
            case "command":
                String command = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                hologram.setClickCommand(sanitizeCommand(command));
                break;
            case "permission":
                hologram.setPermission(sanitizePermission(value));
                break;
            case "viewdistance":
                try {
                    hologram.setViewDistance(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalidNumber"));
                    return;
                }
                break;
            case "updatedistance":
                try {
                    hologram.setUpdateRange(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalidNumber"));
                    return;
                }
                break;
            case "updateinterval":
                try {
                    hologram.setUpdateInterval(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalidNumber"));
                    return;
                }
                break;
            case "textalpha":
                try {
                    hologram.setTextAlpha(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalidNumber"));
                    return;
                }
                break;
            case "textwidth":
                try {
                    hologram.setTextWidth(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalidNumber"));
                    return;
                }
                break;
            case "backgroundcolor":
                hologram.setBackgroundColor(value);
                break;
            case "backgroundalpha":
                try {
                    hologram.setBackgroundAlpha(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalidNumber"));
                    return;
                }
                break;
            case "scale":
                try {
                    hologram.setScale(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalidNumber"));
                    return;
                }
                break;
            case "alignment":
                switch (value.toLowerCase()) {
                    case "left":
                        hologram.setTextAlignment(EclipseHologram.TextAlignment.LEFT);
                        break;
                    case "center":
                        hologram.setTextAlignment(EclipseHologram.TextAlignment.CENTER);
                        break;
                    case "right":
                        hologram.setTextAlignment(EclipseHologram.TextAlignment.RIGHT);
                        break;
                    default:
                        player.sendMessage(plugin.getMessage("invalidAlignment"));
                        return;
                }
                break;
            case "followtype":
                switch (value.toLowerCase()) {
                    case "fixed":
                        hologram.setFollowType(EclipseHologram.FollowType.FIXED);
                        break;
                    case "vertical":
                        hologram.setFollowType(EclipseHologram.FollowType.VERTICAL);
                        break;
                    case "horizontal":
                        hologram.setFollowType(EclipseHologram.FollowType.HORIZONTAL);
                        break;
                    case "center":
                        hologram.setFollowType(EclipseHologram.FollowType.CENTER);
                        break;
                    default:
                        player.sendMessage(plugin.getMessage("invalidFollowType"));
                        return;
                }
                break;
            default:
                player.sendMessage(plugin.getMessage("hologramInvalidSetting").replace("%setting%", setting));
                return;
        }

        plugin.getHologramManager().updateHologram(hologram);
        player.sendMessage(plugin.getMessage("hologramSettingUpdated").replace("%setting%", setting).replace("%value%", value));
    }

    private void handlePage(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("hologramPageUsage"));
            return;
        }

        String name = args[1];
        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        String action = args[2].toLowerCase();
        switch (action) {
            case "next":
                hologram.nextPage(player);
                break;
            case "prev":
                hologram.prevPage(player);
                break;
            default:
                try {
                    int page = Integer.parseInt(action);
                    hologram.setPlayerPage(player, page);
                } catch (NumberFormatException e) {
                    player.sendMessage(plugin.getMessage("invalidNumber"));
                    return;
                }
        }

        plugin.getHologramManager().updateHologram(hologram);
        player.sendMessage(plugin.getMessage("hologramPageChanged").replace("%page%", String.valueOf(hologram.getPlayerPage(player))));
    }

    private void handleToggle(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("hologramToggleUsage"));
            return;
        }

        String name = args[1];
        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        String setting = args[2].toLowerCase();
        switch (setting) {
            case "enabled":
                hologram.setEnabled(!hologram.isEnabled());
                break;
            case "clickable":
                hologram.setClickable(!hologram.isClickable());
                break;
            case "alwaysvisible":
                hologram.setAlwaysVisible(!hologram.isAlwaysVisible());
                break;
            case "particles":
                hologram.setShowParticles(!hologram.isShowParticles());
                break;
            case "textshadow":
                hologram.setTextShadow(!hologram.isTextShadow());
                break;
            case "seethrough":
                hologram.setTextSeeThrough(!hologram.isTextSeeThrough());
                break;
            case "doublesided":
                hologram.setDoubleSided(!hologram.isDoubleSided());
                break;
            case "board":
                hologram.setBoardEnabled(!hologram.isBoardEnabled());
                break;
            case "lineofsight":
                hologram.setLineOfSight(!hologram.isLineOfSight());
                break;
            default:
                player.sendMessage(plugin.getMessage("hologramInvalidToggle").replace("%setting%", setting));
                return;
        }

        plugin.getHologramManager().updateHologram(hologram);
        player.sendMessage(plugin.getMessage("hologramToggled").replace("%setting%", setting));
    }

    private void handleAddLine(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("hologramAddLineUsage"));
            return;
        }

        String name = args[1];
        if (!isValidHologramName(name)) {
            player.sendMessage(plugin.getMessage("hologramInvalidName"));
            return;
        }

        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        String line = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        hologram.addLine(sanitizeLine(line));
        plugin.getHologramManager().updateHologram(hologram);
        player.sendMessage(plugin.getMessage("hologramLineAdded"));
    }

    private void handleRemoveLine(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("hologramRemoveLineUsage"));
            return;
        }

        String name = args[1];
        if (!isValidHologramName(name)) {
            player.sendMessage(plugin.getMessage("hologramInvalidName"));
            return;
        }

        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        try {
            int index = Integer.parseInt(args[2]) - 1;
            hologram.removeLine(index);
            plugin.getHologramManager().updateHologram(hologram);
            player.sendMessage(plugin.getMessage("hologramLineRemoved"));
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalidNumber"));
        }
    }

    private void handleSetLine(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(plugin.getMessage("hologramSetLineUsage"));
            return;
        }

        String name = args[1];
        if (!isValidHologramName(name)) {
            player.sendMessage(plugin.getMessage("hologramInvalidName"));
            return;
        }

        EclipseHologram hologram = plugin.getHologramManager().getHologram(name);
        if (hologram == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", name));
            return;
        }

        try {
            int index = Integer.parseInt(args[2]) - 1;
            String line = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            hologram.setLine(index, sanitizeLine(line));
            plugin.getHologramManager().updateHologram(hologram);
            player.sendMessage(plugin.getMessage("hologramLineSet"));
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalidNumber"));
        }
    }

    private void handleCopy(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("hologramCopyUsage"));
            return;
        }

        String sourceName = args[1];
        String newName = args[2];

        if (!isValidHologramName(sourceName) || !isValidHologramName(newName)) {
            player.sendMessage(plugin.getMessage("hologramInvalidName"));
            return;
        }

        EclipseHologram source = plugin.getHologramManager().getHologram(sourceName);
        if (source == null) {
            player.sendMessage(plugin.getMessage("hologramNotFound").replace("%name%", sourceName));
            return;
        }

        if (plugin.getHologramManager().hologramExists(newName)) {
            player.sendMessage(plugin.getMessage("hologramAlreadyExists").replace("%name%", newName));
            return;
        }

        Location newLoc = player.getLocation().add(0, 1.5, 0);
        if (plugin.getHologramManager().createHologram(newName, newLoc, source.getLines())) {
            EclipseHologram copy = plugin.getHologramManager().getHologram(newName);
            if (copy != null) {
                copy.setViewDistance(source.getViewDistance());
                copy.setAlwaysVisible(source.isAlwaysVisible());
                copy.setUpdateInterval(source.getUpdateInterval());
                copy.setClickable(source.isClickable());
                copy.setClickCommand(source.getClickCommand());
                copy.setPermission(source.getPermission());
                copy.setFollowType(source.getFollowType());
                copy.setTextAlignment(source.getTextAlignment());
                copy.setTextShadow(source.isTextShadow());
                copy.setTextAlpha(source.getTextAlpha());
                copy.setTextWidth(source.getTextWidth());
                copy.setTextSeeThrough(source.isTextSeeThrough());
                copy.setBackgroundColor(source.getBackgroundColor());
                copy.setBackgroundAlpha(source.getBackgroundAlpha());
                copy.setScale(source.getScale());
                copy.setBoardEnabled(source.isBoardEnabled());
                copy.setBoardMaterial(source.getBoardMaterial());
                plugin.getHologramManager().updateHologram(copy);
            }
            player.sendMessage(plugin.getMessage("hologramCopied").replace("%source%", sourceName).replace("%name%", newName));
        } else {
            player.sendMessage(plugin.getMessage("hologramCopyFailed"));
        }
    }

    @Override
    protected boolean perform(CommandSender sender, Player unused, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            return tabCompleteArguments.stream()
                    .filter(arg -> arg.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("delete") || subCommand.equals("edit") || subCommand.equals("move") || subCommand.equals("info") || 
                subCommand.equals("set") || subCommand.equals("page") || subCommand.equals("toggle") || 
                subCommand.equals("addline") || subCommand.equals("removeline") || subCommand.equals("setline")) {
                String prefix = args[1].toLowerCase();
                return plugin.getHologramManager().getHologramNames().stream()
                        .filter(name -> name.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
            if (subCommand.equals("copy")) {
                String prefix = args[1].toLowerCase();
                return plugin.getHologramManager().getHologramNames().stream()
                        .filter(name -> name.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("set")) {
                return List.of("clickable", "command", "permission", "viewdistance", "updatedistance", 
                        "updateinterval", "textalpha", "textwidth", "backgroundcolor", "backgroundalpha", 
                        "scale", "alignment", "followtype").stream()
                        .filter(arg -> arg.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (subCommand.equals("toggle")) {
                return List.of("enabled", "clickable", "alwaysvisible", "particles", "textshadow", 
                        "seethrough", "doublesided", "board", "lineofsight").stream()
                        .filter(arg -> arg.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (subCommand.equals("page")) {
                return List.of("next", "prev").stream()
                        .filter(arg -> arg.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("set")) {
                String setting = args[2].toLowerCase();
                if (setting.equals("alignment")) {
                    return List.of("left", "center", "right");
                }
                if (setting.equals("followtype")) {
                    return List.of("fixed", "vertical", "horizontal", "center");
                }
            }
        }
        return List.of();
    }
}
