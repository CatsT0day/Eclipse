package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Managers.EclipsePermissionManager.CAPIPermissions;
import me.catst0day.Eclipse.Utils.Text.RawJsonMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class Help extends CommandTemplate {

    public Help(Eclipse plugin) {
        super(plugin, "help", List.of("?", "h"), CAPIPermissions.HELP, true, 0, "Show help information");
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        RawJsonMessage msg = new RawJsonMessage();
        msg.addText(plugin.getMessage("helpMenuHeader"));
        
        CommandTemplate.getRegisteredCommands().forEach((name, cmd) -> {
            if (cmd instanceof CommandTemplate command) {
                msg.addText("§a/" + name)
                   .addHover(plugin.getMessage("helpCommandHover").replace("{description}", command.getDescription()))
                   .addCommand(name);
                msg.addText(" ");
            }
        });
        
        msg.show(player);
        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player player, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        return List.of();
    }
}