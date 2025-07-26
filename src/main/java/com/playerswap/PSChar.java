package com.playerswap;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Collection;

public class PSChar {

    private final Player player;
    private final double health;
    private final int foodLevel;
    private final float saturation;
    private final Location location;
    private final ItemStack[] inventory;
    private final Inventory topInventory;
    private final Collection<PotionEffect> potionEffects;
    private Entity vehicle;
    private final Vector velocity;

    public PSChar(Player player) {
        this.player = player;
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.location = player.getLocation();
        this.inventory = player.getInventory().getContents();
        this.topInventory = player.getOpenInventory().getTopInventory();
        this.potionEffects = player.getActivePotionEffects();
        if(player.isInsideVehicle()) {
            this.vehicle = player.getVehicle();
            assert vehicle != null;
            this.velocity = vehicle.getVelocity();
        }else{
            this.velocity = player.getVelocity();
        }
    }

    /*
        Applies the saved information to the given player
        Current Supports:
            - Health
            - Hunger & Saturation
            - Location
            - Inventory
            - Potion Effects
            - Vehicle
            - Velocity
    * */
    public void applyTo(PSConfig config, Player otherPlayer) {
        if(otherPlayer.getName().equals(player.getName())) return; // Return early if it's the same player

        World world = otherPlayer.getWorld();

        if(config.getSwapSetting(SwapAttribute.Location)) {
            // If the original player had a top inventory open, close their inventory then drop
            // the items from the top inventory in the world.
//            if(topInventory.length != 0) {
//                player.closeInventory();
//                for(ItemStack stack : topInventory) {
//                    world.dropItem(player.getLocation(), stack);
//                }
//            }
            player.closeInventory();
            otherPlayer.openInventory(topInventory);


            // If vehicle is null then teleport the player. If not, teleport the vehicle.
            if(vehicle == null) {
                otherPlayer.teleport(location);
                otherPlayer.setVelocity(velocity);
            }else{
                // Make the original player exist vehicle, then teleport the vehicle & new player,
                // then make the new player enter the vehicle & apply vehicle's velocity.
                player.leaveVehicle();
                vehicle.teleport(location.add(0, 0.5d, 0));
                otherPlayer.teleport(vehicle);
                vehicle.addPassenger(otherPlayer);
                vehicle.setVelocity(velocity);
            }
        }
        if(config.getSwapSetting(SwapAttribute.Health)) {
            otherPlayer.setHealth(health);
        }

        if(config.getSwapSetting(SwapAttribute.Hunger)) {
            otherPlayer.setFoodLevel(foodLevel);
        }

        if(config.getSwapSetting(SwapAttribute.Saturation)) {
            otherPlayer.setSaturation(saturation);
        }

        if(config.getSwapSetting(SwapAttribute.Inventory)) {
            otherPlayer.getInventory().setContents(inventory);
        }

        if(config.getSwapSetting(SwapAttribute.PotionEffects)) {
            // Remove all potion effects
            for (PotionEffect effect : otherPlayer.getActivePotionEffects()) {
                otherPlayer.removePotionEffect(effect.getType());
            }
            otherPlayer.addPotionEffects(potionEffects);
        }

        otherPlayer.playSound(otherPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
    }

    public String getPlayerName() {
        return player.getName();
    }
}
