package me.catst0day.Eclipse.Auction;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionListing {
    private final int id;
    private final UUID seller;
    private final ItemStack item;
    private final double price;
    private final long createdAt;
    private final long expiresAt;
    private final String category;
    private boolean sold;

    public AuctionListing(int id, UUID seller, ItemStack item, double price, long createdAt, long expiresAt, String category, boolean sold) {
        this.id = id;
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.category = category;
        this.sold = sold;
    }

    public int getId() {
        return id;
    }

    public UUID getSeller() {
        return seller;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public String getCategory() {
        return category;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAt;
    }
}
