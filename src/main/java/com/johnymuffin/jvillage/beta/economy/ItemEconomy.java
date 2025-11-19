package com.johnymuffin.jvillage.beta.economy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Utility class for managing items as currency
 * Supports any item type configured in settings
 */
public class ItemEconomy {

    private final Material currencyItem;
    private final int maxStackSize;

    public ItemEconomy(Material currencyItem) {
        this.currencyItem = currencyItem;
        this.maxStackSize = currencyItem.getMaxStackSize();
    }

    /**
     * Check if a player has enough of the currency item
     * @param player The player to check
     * @param amount The amount required
     * @return true if the player has enough
     */
    public boolean hasEnough(Player player, int amount) {
        if (amount <= 0) return true;
        
        PlayerInventory inventory = player.getInventory();
        int total = 0;
        
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == currencyItem) {
                total += item.getAmount();
            }
        }
        
        return total >= amount;
    }
    
    /**
     * Remove currency items from a player's inventory
     * @param player The player
     * @param amount The amount to remove
     * @return true if successful, false if not enough items
     */
    public boolean removeItems(Player player, int amount) {
        if (amount <= 0) return true;
        if (!hasEnough(player, amount)) return false;
        
        PlayerInventory inventory = player.getInventory();
        int remaining = amount;
        
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == currencyItem) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    inventory.setItem(i, null);
                    remaining -= itemAmount;
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
                
                if (remaining == 0) break;
            }
        }
        
        return remaining == 0;
    }
    
    /**
     * Give currency items to a player
     * @param player The player
     * @param amount The amount to give
     * @return true if successful
     */
    public boolean giveItems(Player player, int amount) {
        if (amount <= 0) return true;
        
        PlayerInventory inventory = player.getInventory();
        int remaining = amount;
        
        while (remaining > 0) {
            int stackSize = Math.min(remaining, maxStackSize);
            ItemStack itemStack = new ItemStack(currencyItem, stackSize);
            
            // Try to add to inventory
            if (inventory.firstEmpty() != -1 || canAddToExisting(inventory, stackSize)) {
                inventory.addItem(itemStack);
                remaining -= stackSize;
            } else {
                // Drop on ground if inventory is full
                player.getWorld().dropItem(player.getLocation(), itemStack);
                remaining -= stackSize;
            }
        }
        
        return true;
    }
    
    /**
     * Check if items can be added to existing stacks
     */
    private boolean canAddToExisting(PlayerInventory inventory, int amount) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == currencyItem) {
                if (item.getAmount() < maxStackSize) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Get the total amount of currency items a player has
     * @param player The player
     * @return The total amount
     */
    public int getAmount(Player player) {
        PlayerInventory inventory = player.getInventory();
        int total = 0;
        
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == currencyItem) {
                total += item.getAmount();
            }
        }
        
        return total;
    }

    public Material getCurrencyItem() {
        return currencyItem;
    }
}
