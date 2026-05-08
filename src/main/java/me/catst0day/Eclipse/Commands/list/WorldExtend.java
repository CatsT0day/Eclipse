package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldExtend extends CommandTemplate {

    public WorldExtend(Eclipse plugin) {
        super(plugin, "worldextend", List.of("wextend"), null, true, 0, "Extend world border");
        setTabCompleteArguments(List.of("set", "reset", "info", "add", "center"));
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        World world = player.getWorld();
        return execute(player, world, args);
    }

    @Override
    protected boolean perform(CommandSender sender, Player unused, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessage("worldextendUsage"));
            return true;
        }

        if (args.length >= 1) {
            World world = Bukkit.getWorld(args[0]);
            if (world == null) {
                sender.sendMessage(plugin.getMessage("worldNotFound").replace("%world%", args[0]));
                return true;
            }
            
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            return execute(sender, world, newArgs);
        }

        sender.sendMessage(plugin.getMessage("worldextendUsage"));
        return true;
    }

    private boolean execute(CommandSender sender, World world, String[] args) {
        WorldBorder border = world.getWorldBorder();
        
        if (args.length == 0) {
            double size = border.getSize();
            sender.sendMessage(plugin.getMessage("worldextendInfo")
                    .replace("%world%", world.getName())
                    .replace("%size%", String.format("%.1f", size)));
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "set":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getMessage("worldextendSetUsage"));
                    return true;
                }
                try {
                    double size = Double.parseDouble(args[1]);
                    if (size <= 0) {
                        sender.sendMessage(plugin.getMessage("worldextendInvalidSize"));
                        return true;
                    }
                    
                    long seconds = args.length >= 3 ? Long.parseLong(args[2]) : 0;
                    border.setSize(size, seconds);
                    
                    sender.sendMessage(plugin.getMessage("worldextendSet")
                            .replace("%world%", world.getName())
                            .replace("%size%", String.format("%.1f", size))
                            .replace("%time%", seconds > 0 ? String.valueOf(seconds) : "instantly"));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessage("worldextendInvalidNumber"));
                }
                break;

            case "add":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getMessage("worldextendAddUsage"));
                    return true;
                }
                try {
                    double addSize = Double.parseDouble(args[1]);
                    double currentSize = border.getSize();
                    double newSize = currentSize + addSize;
                    
                    if (newSize <= 0) {
                        sender.sendMessage(plugin.getMessage("worldextendInvalidSize"));
                        return true;
                    }
                    
                    long seconds = args.length >= 3 ? Long.parseLong(args[2]) : 0;
                    border.setSize(newSize, seconds);
                    
                    sender.sendMessage(plugin.getMessage("worldextendAdded")
                            .replace("%world%", world.getName())
                            .replace("%added%", String.format("%.1f", addSize))
                            .replace("%size%", String.format("%.1f", newSize))
                            .replace("%time%", seconds > 0 ? String.valueOf(seconds) : "instantly"));
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessage("worldextendInvalidNumber"));
                }
                break;

            case "reset":
                border.reset();
                sender.sendMessage(plugin.getMessage("worldextendReset").replace("%world%", world.getName()));
                break;

            case "info":
                double size = border.getSize();
                double damageAmount = border.getDamageAmount();
                double damageBuffer = border.getDamageBuffer();
                int warningTime = border.getWarningTime();
                int warningDistance = border.getWarningDistance();
                
                sender.sendMessage(plugin.getMessage("worldextendDetailedInfo")
                        .replace("%world%", world.getName())
                        .replace("%size%", String.format("%.1f", size))
                        .replace("%damage%", String.format("%.2f", damageAmount))
                        .replace("%buffer%", String.format("%.1f", damageBuffer))
                        .replace("%warningTime%", String.valueOf(warningTime))
                        .replace("%warningDistance%", String.valueOf(warningDistance)));
                break;

            case "center":
                if (args.length >= 3) {
                    try {
                        double x = Double.parseDouble(args[1]);
                        double z = Double.parseDouble(args[2]);
                        border.setCenter(x, z);
                        sender.sendMessage(plugin.getMessage("worldextendCenterSet")
                                .replace("%world%", world.getName())
                                .replace("%x%", String.format("%.1f", x))
                                .replace("%z%", String.format("%.1f", z)));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(plugin.getMessage("worldextendInvalidNumber"));
                    }
                } else {
                    org.bukkit.Location center = border.getCenter();
                    sender.sendMessage(plugin.getMessage("worldextendCenterInfo")
                            .replace("%world%", world.getName())
                            .replace("%x%", String.format("%.1f", center.getX()))
                            .replace("%z%", String.format("%.1f", center.getZ())));
                }
                break;

            default:
                sender.sendMessage(plugin.getMessage("worldextendUsage"));
        }

        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return List.of("set", "add", "reset", "info", "center").stream()
                    .filter(s -> s.startsWith(prefix))
                    .toList();
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add"))) {
            return List.of("100", "500", "1000", "5000");
        }
        return List.of();
    }
}
