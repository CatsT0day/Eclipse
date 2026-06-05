package me.catst0day.Eclipse.Commands.list;

import me.catst0day.Eclipse.Auction.AuctionGUI;
import me.catst0day.Eclipse.Managers.EclipseAuctionManager;
import me.catst0day.Eclipse.Commands.commandAPI.CommandTemplate;
import me.catst0day.Eclipse.Eclipse;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Auction extends CommandTemplate {

    private final AuctionGUI auctionGUI;

    public Auction(Eclipse plugin) {
        super(plugin, "auction", List.of("ah", "auctionhouse"), null, true, 0, "Auction house commands");
        this.auctionGUI = new AuctionGUI(plugin, plugin.getAuctionManager());
        setTabCompleteArguments(List.of("open", "sell", "cancel", "browse", "my"));
    }

    private EclipseAuctionManager getAuctionManager() {
        return plugin.getAuctionManager();
    }

    @Override
    protected boolean perform(Player player, String[] args) {
        if (args.length == 0) {
            auctionGUI.openMainMenu(player);
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "open":
            case "browse":
                if (args.length == 1) {
                    auctionGUI.openBrowseMenu(player, 1);
                } else {
                    try {
                        int page = Integer.parseInt(args[1]);
                        auctionGUI.openBrowseMenu(player, page);
                    } catch (NumberFormatException e) {
                        player.sendMessage(plugin.getMessage("auctionInvalidPage"));
                    }
                }
                break;

            case "my":
            case "listings":
                auctionGUI.openMyListingsMenu(player);
                break;

            case "sell":
                if (args.length < 2) {
                    player.sendMessage(plugin.getMessage("auctionSellUsage"));
                    return true;
                }
                sellItem(player, args);
                break;

            case "cancel":
                if (args.length < 2) {
                    player.sendMessage(plugin.getMessage("auctionCancelUsage"));
                    return true;
                }
                cancelListing(player, args[1]);
                break;

            case "categories":
                auctionGUI.openCategoriesMenu(player);
                break;

            default:
                auctionGUI.openMainMenu(player);
        }

        return true;
    }

    @Override
    protected boolean perform(CommandSender sender, Player unused, String[] args) {
        sender.sendMessage(plugin.getMessage("playerOnlyCommand"));
        return true;
    }

    private void sellItem(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            player.sendMessage(plugin.getMessage("auctionNoItemInHand"));
            return;
        }

        try {
            double price = Double.parseDouble(args[1]);
            if (price <= 0) {
                player.sendMessage(plugin.getMessage("auctionInvalidPrice"));
                return;
            }

            int listingCount = getAuctionManager().getPlayerListingCount(player.getUniqueId());
            int maxListings = plugin.getConfig().getInt("auction.maxListingsPerPlayer", 10);
            if (listingCount >= maxListings) {
                player.sendMessage(plugin.getMessage("auctionMaxListingsReached").replace("%max%", String.valueOf(maxListings)));
                return;
            }

            String category = args.length >= 3 ? args[2].toLowerCase() : getAuctionManager().getCategoryForItem(item);
            int listingId = getAuctionManager().createListing(player.getUniqueId(), item, price, category);

            if (listingId != -1) {
                player.getInventory().setItemInMainHand(null);
                player.sendMessage(plugin.getMessage("auctionListed")
                        .replace("%item%", item.getType().name())
                        .replace("%price%", String.format("%.2f", price))
                        .replace("%id%", String.valueOf(listingId)));
            } else {
                player.sendMessage(plugin.getMessage("auctionListFailed"));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("auctionInvalidPrice"));
        }
    }

    private void cancelListing(Player player, String idStr) {
        try {
            int listingId = Integer.parseInt(idStr);
            if (getAuctionManager().cancelListing(listingId, player.getUniqueId())) {
                player.sendMessage(plugin.getMessage("auctionCancelled"));
            } else {
                player.sendMessage(plugin.getMessage("auctionCancelFailed"));
            }
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("auctionInvalidId"));
        }
    }

    @Override
    protected List<String> tabCompl(Player player, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return List.of("open", "browse", "my", "sell", "cancel", "categories").stream()
                    .filter(s -> s.startsWith(prefix))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("sell")) {
            return List.of("100", "500", "1000", "5000");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("sell")) {
            return List.of("weapons", "armor", "tools", "blocks", "food", "misc");
        }
        return List.of();
    }
}
