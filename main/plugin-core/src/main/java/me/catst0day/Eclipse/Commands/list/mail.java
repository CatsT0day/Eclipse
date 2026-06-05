package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Managers.EclipseMailManager;
import me.catst0day.Eclipse.Mail.MailTemplate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class mail extends CommandTemplate {

    private final EclipseMailManager mailManager;

    public mail(Eclipse plugin, EclipseMailManager mailManager) {
        super(plugin, "mail", List.of("email"), null, true, 0, "Mail system commands");
        this.mailManager = mailManager;
        setTabCompleteArguments(List.of("send", "read", "inbox", "clear", "delete"));
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length == 0) {
            showInbox(player);
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "send":
                if (args.length < 3) {
                    player.sendMessage(plugin.getMessage("mailSendUsage"));
                    return true;
                }
                sendMail(player, args);
                break;

            case "read":
                if (args.length == 1) {
                    showInbox(player);
                } else {
                    readMail(player, args[1]);
                }
                break;

            case "inbox":
                showInbox(player);
                break;

            case "clear":
                clearMail(player);
                break;

            case "delete":
                if (args.length < 2) {
                    player.sendMessage(plugin.getMessage("mailDeleteUsage"));
                    return true;
                }
                deleteMail(player, args[1]);
                break;

            default:
                player.sendMessage(plugin.getMessage("mailUsage"));
        }

        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player unused, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    private void sendMail(Player sender, String[] args) {
        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(plugin.getMessage("mailPlayerNotFound").replace("%player%", targetName));
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        int mailId = Eclipse.getI().getMailManager().sendMail(sender.getUniqueId(), target.getUniqueId(), message.toString().trim());

        if (mailId != -1) {
            sender.sendMessage(plugin.getMessage("mailSent")
                    .replace("%player%", targetName)
                    .replace("%message%", message.toString().trim()));
        } else {
            sender.sendMessage(plugin.getMessage("mailSendFailed"));
        }
    }

    private void showInbox(Player player) {
        List<MailTemplate> mailList = mailManager.getPlayerMail(player.getUniqueId());
        int unreadCount = mailManager.getUnreadCount(player.getUniqueId());

        if (mailList.isEmpty()) {
            player.sendMessage(plugin.getMessage("mailEmpty"));
            return;
        }

        player.sendMessage(plugin.getMessage("mailInboxHeader")
                .replace("%unread%", String.valueOf(unreadCount))
                .replace("%total%", String.valueOf(mailList.size())));

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
        int page = 1;
        int perPage = 10;
        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, mailList.size());

        for (int i = start; i < end; i++) {
            MailTemplate mail = mailList.get(i);
            OfflinePlayer sender = Bukkit.getOfflinePlayer(mail.getSender());
            String date = sdf.format(new Date(mail.getTimestamp()));
            String readStatus = mail.isRead() ? "§7" : "§a";

            player.sendMessage(plugin.getMessage("mailInboxItem")
                    .replace("%id%", String.valueOf(mail.getId()))
                    .replace("%sender%", sender.getName())
                    .replace("%date%", date)
                    .replace("%read%", readStatus)
                    .replace("%message%", mail.getMessage().length() > 30 ? 
                            mail.getMessage().substring(0, 30) + "..." : mail.getMessage()));
        }

        if (mailList.size() > perPage) {
            player.sendMessage(plugin.getMessage("mailInboxFooter")
                    .replace("%page%", String.valueOf(page))
                    .replace("%totalpages%", String.valueOf((int) Math.ceil((double) mailList.size() / perPage))));
        }
    }

    private void readMail(Player player, String idStr) {
        try {
            int mailId = Integer.parseInt(idStr);
            List<MailTemplate> mailList = mailManager.getPlayerMail(player.getUniqueId());

            MailTemplate mail = mailList.stream()
                    .filter(m -> m.getId() == mailId)
                    .findFirst()
                    .orElse(null);

            if (mail == null) {
                player.sendMessage(plugin.getMessage("mailNotFound"));
                return;
            }

            mailManager.markAsRead(mailId);

            OfflinePlayer sender = Bukkit.getOfflinePlayer(mail.getSender());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
            String date = sdf.format(new Date(mail.getTimestamp()));

            player.sendMessage(plugin.getMessage("mailReadHeader")
                    .replace("%id%", String.valueOf(mail.getId()))
                    .replace("%sender%", sender.getName())
                    .replace("%date%", date));
            player.sendMessage(plugin.getMessage("mailReadContent").replace("%message%", mail.getMessage()));
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("mailInvalidId"));
        }
    }

    private void clearMail(Player player) {
        if (mailManager.clearAllMail(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("mailCleared"));
        } else {
            player.sendMessage(plugin.getMessage("mailClearFailed"));
        }
    }

    private void deleteMail(Player player, String idStr) {
        try {
            int mailId = Integer.parseInt(idStr);
            if (mailManager.deleteMail(mailId)) {
                player.sendMessage(plugin.getMessage("mailDeleted").replace("%id%", idStr));
            } else {
                player.sendMessage(plugin.getMessage("mailDeleteFailed"));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("mailInvalidId"));
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return List.of("send", "read", "inbox", "clear", "delete").stream()
                    .filter(s -> s.startsWith(prefix))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
