package com.playerswap;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;

public class PSChar {

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
        otherPlayer.setHealth(health);
        otherPlayer.setFoodLevel(foodLevel);
        otherPlayer.setSaturation(saturation);
        otherPlayer.teleport(location);
        otherPlayer.getInventory().setContents(inventory);
        otherPlayer.getActivePotionEffects().clear();
        otherPlayer.getActivePotionEffects().addAll(potionEffects);
        //System.out.println("Swapping " + playerName + " and " + otherPlayer.getName() + "...");
        //System.out.println("New stats... Health: " + health + ". FoodLevel: " + foodLevel + ". Location: " + location.getX() + "," + location.getY() + "," + location.getZ());

        otherPlayer.playSound(otherPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
    }

    public String getPlayerName() {
        return playerName;
    }
}
