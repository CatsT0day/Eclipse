package me.catst0day.Eclipse.Auction;

import me.catst0day.Eclipse.Eclipse;
import me.catst0day.Eclipse.Entity.Player.Gui;
import me.catst0day.Eclipse.Entity.Player.GuiButton;
import me.catst0day.Eclipse.Managers.EclipseAuctionManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AuctionGUI {
    private final Eclipse plugin;
    private final EclipseAuctionManager auctionManager;

    public AuctionGUI(Eclipse plugin, EclipseAuctionManager auctionManager) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;
    }

    public void openMainMenu(Player player) {
        Gui gui = new Gui(player, plugin.getMessage("auctionMainMenuTitle"), 3);
        GuiButton browseButton = new GuiButton(Material.CHEST)
                .setName(plugin.getMessage("auctionBrowseButton"))
                .addLore(plugin.getMessage("auctionBrowseButtonLore"))
                .onLeftClick(p -> openBrowseMenu(p, 1))
                .closeOnClick();
        gui.addButton(11, browseButton);
        GuiButton myListingsButton = new GuiButton(Material.ENDER_CHEST)
                .setName(plugin.getMessage("auctionMyListingsButton"))
                .addLore(plugin.getMessage("auctionMyListingsButtonLore"))
                .onLeftClick(p -> openMyListingsMenu(p))
                .closeOnClick();
        gui.addButton(13, myListingsButton);
        GuiButton sellButton = new GuiButton(Material.ANVIL)
                .setName(plugin.getMessage("auctionSellButton"))
                .addLore(plugin.getMessage("auctionSellButtonLore"))
                .onLeftClick(p -> openSellMenu(p))
                .closeOnClick();
        gui.addButton(15, sellButton);

        GuiButton categoriesButton = new GuiButton(Material.BOOKSHELF)
                .setName(plugin.getMessage("auctionCategoriesButton"))
                .addLore(plugin.getMessage("auctionCategoriesButtonLore"))
                .onLeftClick(p -> openCategoriesMenu(p))
                .closeOnClick();
        gui.addButton(22, categoriesButton);

        gui.open();
    }

    public void openBrowseMenu(Player player, int page) {
        List<AuctionListing> listings = auctionManager.getAllListings();
        int itemsPerPage = 45;
        int totalPages = (int) Math.ceil((double) listings.size() / itemsPerPage);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        final int currentPage = page;

        Gui gui = new Gui(player, plugin.getMessage("auctionBrowseTitle").replace("%page%", String.valueOf(page)), 6);

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, listings.size());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");

        for (int i = 0; i < endIndex - startIndex; i++) {
            AuctionListing listing = listings.get(startIndex + i);
            ItemStack item = listing.getItem();
            if (item == null) continue;

            GuiButton itemButton = new GuiButton(item.getType())
                    .setName(plugin.getMessage("auctionListingItemName")
                            .replace("%item%", item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? 
                                    item.getItemMeta().getDisplayName() : item.getType().name()))
                    .addLore(plugin.getMessage("auctionListingPrice").replace("%price%", String.format("%.2f", listing.getPrice())))
                    .addLore(plugin.getMessage("auctionListingSeller").replace("%seller%", 
                            plugin.getServer().getOfflinePlayer(listing.getSeller()).getName()))
                    .addLore(plugin.getMessage("auctionListingExpires").replace("%time%", 
                            sdf.format(new Date(listing.getExpiresAt()))))
                    .addLore(plugin.getMessage("auctionListingClickToBuy"))
                    .onLeftClick(p -> buyListing(p, listing))
                    .closeOnClick();
            gui.addButton(i, itemButton);
        }
        if (page > 1) {
            GuiButton prevButton = new GuiButton(Material.ARROW)
                    .setName(plugin.getMessage("auctionPrevPage"))
                    .onLeftClick(p -> openBrowseMenu(p, currentPage - 1))
                    .closeOnClick();
            gui.addButton(45, prevButton);
        }

        if (page < totalPages) {
            GuiButton nextButton = new GuiButton(Material.ARROW)
                    .setName(plugin.getMessage("auctionNextPage"))
                    .onLeftClick(p -> openBrowseMenu(p, currentPage + 1))
                    .closeOnClick();
            gui.addButton(53, nextButton);
        }
        GuiButton backButton = new GuiButton(Material.BARRIER)
                .setName(plugin.getMessage("auctionBackButton"))
                .onLeftClick(p -> openMainMenu(p))
                .closeOnClick();
        gui.addButton(49, backButton);

        gui.open();
    }

    public void openMyListingsMenu(Player player) {
        List<AuctionListing> listings = auctionManager.getPlayerListings(player.getUniqueId());
        Gui gui = new Gui(player, plugin.getMessage("auctionMyListingsTitle"), 6);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");

        for (int i = 0; i < Math.min(listings.size(), 45); i++) {
            AuctionListing listing = listings.get(i);
            ItemStack item = listing.getItem();
            if (item == null) continue;

            GuiButton itemButton = new GuiButton(item.getType())
                    .setName(plugin.getMessage("auctionListingItemName")
                            .replace("%item%", item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? 
                                    item.getItemMeta().getDisplayName() : item.getType().name()))
                    .addLore(plugin.getMessage("auctionListingPrice").replace("%price%", String.format("%.2f", listing.getPrice())))
                    .addLore(plugin.getMessage("auctionListingExpires").replace("%time%", 
                            sdf.format(new Date(listing.getExpiresAt()))))
                    .addLore(plugin.getMessage("auctionListingStatus").replace("%status%", 
                            listing.isSold() ? plugin.getMessage("auctionStatusSold") : 
                                    listing.isExpired() ? plugin.getMessage("auctionStatusExpired") : 
                                            plugin.getMessage("auctionStatusActive")))
                    .addLore(plugin.getMessage("auctionListingRightClickToCancel"))
                    .onRightClick(p -> cancelListing(p, listing))
                    .closeOnClick();
            gui.addButton(i, itemButton);
        }

        // Back button
        GuiButton backButton = new GuiButton(Material.BARRIER)
                .setName(plugin.getMessage("auctionBackButton"))
                .onLeftClick(p -> openMainMenu(p))
                .closeOnClick();
        gui.addButton(49, backButton);

        gui.open();
    }

    public void openSellMenu(Player player) {
        Gui gui = new Gui(player, plugin.getMessage("auctionSellTitle"), 1);

        // Info button
        GuiButton infoButton = new GuiButton(Material.BOOK)
                .setName(plugin.getMessage("auctionSellInfo"))
                .addLore(plugin.getMessage("auctionSellInfoLore1"))
                .addLore(plugin.getMessage("auctionSellInfoLore2"))
                .addLore(plugin.getMessage("auctionSellInfoLore3"));
        gui.addButton(4, infoButton);

        // Back button
        GuiButton backButton = new GuiButton(Material.BARRIER)
                .setName(plugin.getMessage("auctionBackButton"))
                .onLeftClick(p -> openMainMenu(p))
                .closeOnClick();
        gui.addButton(8, backButton);

        gui.open();

        // Prompt for item selection (handled via command)
        player.sendMessage(plugin.getMessage("auctionSellPrompt"));
    }

    public void openCategoriesMenu(Player player) {
        Gui gui = new Gui(player, plugin.getMessage("auctionCategoriesTitle"), 3);

        String[] categories = {"weapons", "armor", "tools", "blocks", "food", "music", "misc"};
        Material[] icons = {Material.DIAMOND_SWORD, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_PICKAXE, 
                          Material.DIRT, Material.BREAD, Material.MUSIC_DISC_CAT, Material.BARRIER};

        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];
            GuiButton categoryButton = new GuiButton(icons[i])
                    .setName(plugin.getMessage("auctionCategory" + category.substring(0, 1).toUpperCase() + category.substring(1)))
                    .onLeftClick(p -> openCategoryMenu(p, category, 1))
                    .closeOnClick();
            gui.addButton(10 + i, categoryButton);
        }

        // Back button
        GuiButton backButton = new GuiButton(Material.BARRIER)
                .setName(plugin.getMessage("auctionBackButton"))
                .onLeftClick(p -> openMainMenu(p))
                .closeOnClick();
        gui.addButton(22, backButton);

        gui.open();
    }

    public void openCategoryMenu(Player player, String category, int page) {
        List<AuctionListing> listings = auctionManager.getListingsByCategory(category);
        int itemsPerPage = 45;
        int totalPages = (int) Math.ceil((double) listings.size() / itemsPerPage);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        final int currentPage = page;
        final String currentCategory = category;

        Gui gui = new Gui(player, plugin.getMessage("auctionCategoryTitle").replace("%category%", category).replace("%page%", String.valueOf(page)), 6);

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, listings.size());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");

        for (int i = 0; i < endIndex - startIndex; i++) {
            AuctionListing listing = listings.get(startIndex + i);
            ItemStack item = listing.getItem();
            if (item == null) continue;

            GuiButton itemButton = new GuiButton(item.getType())
                    .setName(plugin.getMessage("auctionListingItemName")
                            .replace("%item%", item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? 
                                    item.getItemMeta().getDisplayName() : item.getType().name()))
                    .addLore(plugin.getMessage("auctionListingPrice").replace("%price%", String.format("%.2f", listing.getPrice())))
                    .addLore(plugin.getMessage("auctionListingSeller").replace("%seller%", 
                            plugin.getServer().getOfflinePlayer(listing.getSeller()).getName()))
                    .addLore(plugin.getMessage("auctionListingExpires").replace("%time%", 
                            sdf.format(new Date(listing.getExpiresAt()))))
                    .addLore(plugin.getMessage("auctionListingClickToBuy"))
                    .onLeftClick(p -> buyListing(p, listing))
                    .closeOnClick();
            gui.addButton(i, itemButton);
        }

        // Navigation buttons
        if (page > 1) {
            GuiButton prevButton = new GuiButton(Material.ARROW)
                    .setName(plugin.getMessage("auctionPrevPage"))
                    .onLeftClick(p -> openCategoryMenu(p, currentCategory, currentPage - 1))
                    .closeOnClick();
            gui.addButton(45, prevButton);
        }

        if (page < totalPages) {
            GuiButton nextButton = new GuiButton(Material.ARROW)
                    .setName(plugin.getMessage("auctionNextPage"))
                    .onLeftClick(p -> openCategoryMenu(p, currentCategory, currentPage + 1))
                    .closeOnClick();
            gui.addButton(53, nextButton);
        }

        // Back button
        GuiButton backButton = new GuiButton(Material.BARRIER)
                .setName(plugin.getMessage("auctionBackButton"))
                .onLeftClick(p -> openCategoriesMenu(p))
                .closeOnClick();
        gui.addButton(49, backButton);

        gui.open();
    }

    private void buyListing(Player player, AuctionListing listing) {
        if (listing.isSold()) {
            player.sendMessage(plugin.getMessage("auctionAlreadySold"));
            return;
        }

        if (listing.isExpired()) {
            player.sendMessage(plugin.getMessage("auctionListingExpired"));
            return;
        }

        if (listing.getSeller().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("auctionCannotBuyOwn"));
            return;
        }

        double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
        if (balance < listing.getPrice()) {
            player.sendMessage(plugin.getMessage("auctionInsufficientFunds")
                    .replace("%price%", String.format("%.2f", listing.getPrice()))
                    .replace("%balance%", String.format("%.2f", balance)));
            return;
        }

        if (auctionManager.buyListing(listing.getId(), player.getUniqueId())) {
            player.getInventory().addItem(listing.getItem());
            player.sendMessage(plugin.getMessage("auctionPurchased")
                    .replace("%item%", listing.getItem().getType().name())
                    .replace("%price%", String.format("%.2f", listing.getPrice())));
        } else {
            player.sendMessage(plugin.getMessage("auctionPurchaseFailed"));
        }
    }

    private void cancelListing(Player player, AuctionListing listing) {
        if (!listing.getSeller().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("auctionNotYourListing"));
            return;
        }

        if (listing.isSold()) {
            player.sendMessage(plugin.getMessage("auctionAlreadySold"));
            return;
        }

        if (auctionManager.cancelListing(listing.getId(), player.getUniqueId())) {
            player.getInventory().addItem(listing.getItem());
            player.sendMessage(plugin.getMessage("auctionCancelled"));
        } else {
            player.sendMessage(plugin.getMessage("auctionCancelFailed"));
        }
    }
}
