package me.catst0day.Eclipse.Entity.Player;

import java.lang.reflect.Method;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class EclipsePlayerInventory {

    private static Method topInventoryMethod = null;

    
    public static ItemStack[] getTopInventoryContents(Player player) {
        Inventory inv = getTopInventory(player);
        if (inv == null) return new ItemStack[0];
        return inv.getContents();
    }

    
    public static Inventory getTopInventory(Player player) {
        if (player == null) return null;
        InventoryView view = player.getOpenInventory();

        try {
            if (topInventoryMethod == null) {
                topInventoryMethod = InventoryView.class.getMethod("getTopInventory");
            }
            return (Inventory) topInventoryMethod.invoke(view);
        } catch (Exception e) {
            try {
                return view.getTopInventory();
            } catch (NoSuchMethodError err) {
                return null;
            }
        }
    }
}