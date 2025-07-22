package com.playerswap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PSChar {

    private ItemStack[] inventory;
    private double health;
    private Location location;

    public PSChar fromPlayer(Player player) {
        this.inventory = player.getInventory().getContents();
        this.health = player.getHealth();
        this.location = player.getLocation();
        return this;
    }

    public void swapTo(Player otherPlayer) {

    }

}
