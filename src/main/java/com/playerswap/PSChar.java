package com.playerswap;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PSChar {

    private static HashSet<InventoryType> INV_TYPES = new HashSet<>(Set.of(InventoryType.CRAFTING, InventoryType.WORKBENCH, InventoryType.ANVIL));

    private String playerName;
    private double health;
    private int foodLevel;
    private float saturation;
    private Location location;
    private ItemStack[] inventory;
    private Collection<PotionEffect> potionEffects;

    public PSChar fromPlayer(Player player) {
        this.playerName = player.getName();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.location = player.getLocation();
        this.inventory = player.getInventory().getContents();
        this.potionEffects = player.getActivePotionEffects();
        return this;
    }

    /*
        Applies the saved information to the given player
        Current Supports:
            - Health
            - Hunger & Saturation
            - Location
            - Inventory
            - Potion Effects
    * */
    public void applyTo(Player otherPlayer) {
        if(otherPlayer.getName().equals(playerName)) return; // Return early if it's the same player

        World world = otherPlayer.getWorld();

        // Check if the player has an item on their cursor, then remove it and drop it in the world
        ItemStack cursor = otherPlayer.getItemOnCursor();
        if(!cursor.getType().isAir()) {
            otherPlayer.setItemOnCursor(null);
            world.dropItemNaturally(otherPlayer.getLocation(), cursor);
        }

        // Check if a player had an external inventory open that drops items, then drop the items in the world.
        Inventory topInv = otherPlayer.getOpenInventory().getTopInventory();
        if(INV_TYPES.contains(topInv.getType())) {
            ItemStack[] contents = topInv.getContents();
            topInv.clear();
            // Drop all items except the output slot items. With the exception being anvils.
            int cutoffIndex = contents.length - (topInv.getType().equals(InventoryType.ANVIL) ? 0 : 1);
            for(int i = 0; i < cutoffIndex; i++) {
                world.dropItemNaturally(otherPlayer.getLocation(), contents[i]);
            }
        }

        otherPlayer.setHealth(health);
        otherPlayer.setFoodLevel(foodLevel);
        otherPlayer.setSaturation(saturation);
        otherPlayer.teleport(location);
        otherPlayer.getInventory().setContents(inventory);
        otherPlayer.getActivePotionEffects().clear();
        otherPlayer.getActivePotionEffects().addAll(potionEffects);

        otherPlayer.playSound(otherPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
    }

    public String getPlayerName() {
        return playerName;
    }
}
