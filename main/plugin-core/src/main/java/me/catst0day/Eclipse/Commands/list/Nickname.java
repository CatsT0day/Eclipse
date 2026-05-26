package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Nickname extends CommandTemplate {

    public Nickname(Eclipse plugin) {
        super(plugin, "nickname", List.of("nick"), null, true, 0, "Set your nickname");
        setTabCompleteArguments(List.of("set", "clear", "reset"));
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length == 0) {
            String currentNick = plugin.getChatManager().getNickname(player.getUniqueId());
            if (currentNick != null) {
                player.sendMessage(plugin.getMessage("nicknameCurrent").replace("%nick%", currentNick));
            } else {
                player.sendMessage(plugin.getMessage("nicknameNone"));
            }
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("clear") || action.equals("reset")) {
            plugin.getChatManager().setNickname(player.getUniqueId(), null);
            player.sendMessage(plugin.getMessage("nicknameCleared"));
            return true;
        }

        if (action.equals("set")) {
            if (args.length < 2) {
                player.sendMessage(plugin.getMessage("nicknameUsage"));
                return true;
            }
            String nickname = args[1];
            if (nickname.length() > 16) {
                player.sendMessage(plugin.getMessage("nicknameTooLong"));
                return true;
            }
            if (!nickname.matches("^[a-zA-Z0-9_&§]+$")) {
                player.sendMessage(plugin.getMessage("nicknameInvalidChars"));
                return true;
            }

            plugin.getChatManager().setNickname(player.getUniqueId(), nickname);
            player.sendMessage(plugin.getMessage("nicknameSet").replace("%nick%", nickname));
            return true;
        }
        if (args.length >= 1) {
            String nickname = args[0];
            
            if (nickname.length() > 16) {
                player.sendMessage(plugin.getMessage("nicknameTooLong"));
                return true;
            }

            if (!nickname.matches("^[a-zA-Z0-9_&§]+$")) {
                player.sendMessage(plugin.getMessage("nicknameInvalidChars"));
                return true;
            }

            plugin.getChatManager().setNickname(player.getUniqueId(), nickname);
            player.sendMessage(plugin.getMessage("nicknameSet").replace("%nick%", nickname));
            return true;
        }

        player.sendMessage(plugin.getMessage("nicknameUsage"));
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        if (args.length >= 2) {
            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);
            
            if (target == null) {
                sender.sendMessage(plugin.getMessage("playerNotFound"));
                return true;
            }

            if (!sender.hasPermission("eclipse.nickname.others")) {
                sender.sendMessage(plugin.getMessage("noPermission"));
                return true;
            }

            String action = args[1].toLowerCase();
            
            if (action.equals("clear") || action.equals("reset")) {
                plugin.getChatManager().setNickname(target.getUniqueId(), null);
                sender.sendMessage(plugin.getMessage("nicknameClearedOther").replace("%player%", target.getName()));
                return true;
            }

            if (args.length >= 3) {
                String nickname = args[2];
                
                if (nickname.length() > 16) {
                    sender.sendMessage(plugin.getMessage("nicknameTooLong"));
                    return true;
                }

                if (!nickname.matches("^[a-zA-Z0-9_&§]+$")) {
                    sender.sendMessage(plugin.getMessage("nicknameInvalidChars"));
                    return true;
                }

                plugin.getChatManager().setNickname(target.getUniqueId(), nickname);
                sender.sendMessage(plugin.getMessage("nicknameSetOther").replace("%player%", target.getName()).replace("%nick%", nickname));
                return true;
            }
        }

        sender.sendMessage(plugin.getMessage("nicknameUsageAdmin"));
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return List.of("set", "clear", "reset").stream()
                    .filter(s -> s.startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
